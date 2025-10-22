package com.perflog.domain.member.service

import com.perflog.common.error.CustomException
import com.perflog.common.error.ErrorCode
import com.perflog.config.security.jwt.JwtUtil
import com.perflog.domain.member.dto.TokenResponse
import com.perflog.domain.member.model.RefreshToken
import com.perflog.domain.member.repository.MemberRepository
import com.perflog.domain.member.repository.RefreshTokenRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class TokenService(
    private val jwtUtil: JwtUtil,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val memberRepository: MemberRepository
) {
    private val log = LoggerFactory.getLogger(TokenService::class.java)

    fun refreshAccessToken(refreshToken: String): TokenResponse? {
        return try {
            // Refresh Token 검증
            if (jwtUtil.isExpired(refreshToken)) {
                return null
            }

            val tokenType = jwtUtil.getTokenType(refreshToken)
            if (tokenType != "refresh") {
                return null
            }

            val memberId = jwtUtil.getMemberId(refreshToken) ?: return null

            // DB에서 Refresh Token 확인
            val storedToken = refreshTokenRepository.findByToken(refreshToken) ?: return null
            if (storedToken.member.id != memberId || storedToken.expiresAt.isBefore(LocalDateTime.now())) {
                refreshTokenRepository.delete(storedToken)
                return null
            }

            // 사용자 정보 조회
            val member = memberRepository.findById(memberId)
                .orElseThrow { throw CustomException(ErrorCode.MEMBER_NOT_FOUND) }

            // 새로운 Access/Refresh Token 생성
            val newAccessToken = jwtUtil.createAccessToken(member.id, member.role.toString())
            val newRefreshToken = jwtUtil.createRefreshToken(member.id)

            refreshTokenRepository.delete(storedToken)

            val refreshTokenEntity = RefreshToken(
                member, newRefreshToken, LocalDateTime.now().plusDays(1)
            )
            refreshTokenRepository.save(refreshTokenEntity)

            TokenResponse(newAccessToken, newRefreshToken)
        } catch (e: Exception) {
            log.warn("Invalid refresh token", e)
            return null
        }
    }

    fun revokeRefreshToken(refreshToken: String) {
        refreshTokenRepository.findByToken(refreshToken)?.let { token ->
            refreshTokenRepository.delete(token)
        }
    }
}