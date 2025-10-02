package com.perflog.config.security.jwt

import com.fasterxml.jackson.databind.ObjectMapper
import com.perflog.common.error.CustomException
import com.perflog.common.error.ErrorCode
import com.perflog.domain.member.dto.MemberDto
import com.perflog.domain.member.model.RefreshToken
import com.perflog.domain.member.repository.MemberRepository
import com.perflog.domain.member.repository.RefreshTokenRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import java.time.LocalDateTime

class LoginFilter(
    private val authenticationManager: AuthenticationManager,
    private val jwtUtil: JwtUtil,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val memberRepository: MemberRepository,
    private val objectMapper: ObjectMapper
) : UsernamePasswordAuthenticationFilter() {

    init {
        setFilterProcessesUrl("/api/member/login")
    }

    override fun attemptAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Authentication {
        val requestBody = request.inputStream
        val loginRequest = objectMapper.readValue(requestBody, MemberDto.LoginRequest::class.java)

        val email = loginRequest.email
        val password = loginRequest.password

        val authRequest = UsernamePasswordAuthenticationToken(email, password, null) // 인증 요청 객체 생성
        return authenticationManager.authenticate(authRequest) // 인증 시도
    }

    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        val userDetails = authResult.principal as UserDetails
        val email = userDetails.username

        val member = memberRepository.findByEmail(email)
            ?: throw CustomException(ErrorCode.MEMBER_NOT_FOUND)

        // 기존 Refresh Token 삭제
        refreshTokenRepository.deleteByMember(member)

        // 새로운 토큰들 생성
        val accessToken = jwtUtil.createAccessToken(member.id, member.role.toString())
        val refreshToken = jwtUtil.createRefreshToken(member.id)

        // Refresh Token DB에 저장
        val refreshTokenEntity = RefreshToken(
            member, refreshToken, LocalDateTime.now().plusDays(1)
        )
        refreshTokenRepository.save(refreshTokenEntity)

        val accessTokenCookie = Cookie("accessToken", accessToken).apply {
            isHttpOnly = true
            secure = false
            path = "/"
            maxAge = 15 * 60 // 15분
        }

        val refreshTokenCookie = Cookie("refreshToken", refreshToken).apply {
            isHttpOnly = true
            secure = false
            path = "/"
            maxAge = 24 * 60 * 60 // 1일
        }

        response.addCookie(accessTokenCookie)
        response.addCookie(refreshTokenCookie)

        response.contentType = "application/json; charset=UTF-8"
        response.status = HttpServletResponse.SC_OK
        response.writer.write(
            objectMapper.writeValueAsString(
                mapOf("message" to "로그인 성공")
            )
        )
    }

    override fun unsuccessfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        failed: AuthenticationException
    ) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json; charset=UTF-8"
        response.writer.write(
            objectMapper.writeValueAsString(
                mapOf("error" to "로그인 실패")
            )
        )
    }
}