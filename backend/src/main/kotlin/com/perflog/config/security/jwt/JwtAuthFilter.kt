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

        if (accessToken != null) {
            try {
                if (!jwtUtil.isExpired(accessToken)) {
                    val tokenType = jwtUtil.getTokenType(accessToken)

                    // Access Token만 인증에 사용
                    if (tokenType == "access") {
                        val memberId = jwtUtil.getMemberId(accessToken)
                        val role = jwtUtil.getRole(accessToken)

                        if (memberId != null && role != null) {
                            val authorities = listOf(SimpleGrantedAuthority(role))
                            val member = memberRepository.findById(memberId)
                                .orElseThrow { throw CustomException(ErrorCode.MEMBER_NOT_FOUND) }


                            val authentication = UsernamePasswordAuthenticationToken(
                                member.email, null, authorities
                            )

                            // 인증 등록
                            SecurityContextHolder.getContext().authentication = authentication
                        }
                    }
                }
            } catch (e: Exception) {
                // 토큰이 유효하지 않은 경우 무시하고 다음 필터로
            }
        }

        filterChain.doFilter(request, response)
    }
}