package com.perflog.config.security.jwt

import com.perflog.common.error.CustomException
import com.perflog.common.error.ErrorCode
import com.perflog.domain.member.repository.MemberRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException

class JwtAuthFilter(
    private val jwtUtil: JwtUtil,
    private val memberRepository: MemberRepository
) : OncePerRequestFilter() { // 서블릿 필터

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // 쿠키에서 Access Token 추출
        val accessToken = request.cookies?.find { it.name == "accessToken" }?.value

        if (accessToken.isNullOrBlank()) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            if (jwtUtil.isExpired(accessToken)) {
                throw CustomException(ErrorCode.EXPIRED_TOKEN)
            }

            if (jwtUtil.getTokenType(accessToken) != "access") {
                throw CustomException(ErrorCode.INVALID_TOKEN)
            }

            val memberId = jwtUtil.getMemberId(accessToken)
                ?: throw CustomException(ErrorCode.INVALID_TOKEN)
            val role = jwtUtil.getRole(accessToken)
                ?: throw CustomException(ErrorCode.INVALID_TOKEN)

            val member = memberRepository.findById(memberId)
                .orElseThrow { CustomException(ErrorCode.MEMBER_NOT_FOUND) }

            val authentication = UsernamePasswordAuthenticationToken(
                member.email, null, listOf(SimpleGrantedAuthority(role))
            )

            // 인증 등록
            SecurityContextHolder.getContext().authentication = authentication
            filterChain.doFilter(request, response)
        } catch (e: Exception) {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            response.contentType = "application/json;charset=UTF-8"
            response.writer.write("""{"error":"ERROR_ACCESS_TOKEN","message":"${e.message}"}""")
            return
        }
    }
}