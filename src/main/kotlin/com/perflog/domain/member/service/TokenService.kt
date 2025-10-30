package com.perflog.domain.member.service

import com.perflog.common.error.CustomException
import com.perflog.common.error.ErrorCode
import com.perflog.config.security.jwt.JwtUtil
import com.perflog.domain.member.dto.TokenResponse
import com.perflog.domain.member.model.RefreshToken
import com.perflog.domain.member.repository.RefreshTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class TokenService(
    private val jwtUtil: JwtUtil,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    fun refreshAccessToken(refreshToken: String): TokenResponse {
        // 토큰 만료/타입 검증
        if (jwtUtil.isExpired(refreshToken)) {
            refreshTokenRepository.deleteByToken(refreshToken)
            throw CustomException(ErrorCode.EXPIRED_TOKEN)
        }

        if (jwtUtil.getTokenType(refreshToken) != "refresh") {
            throw CustomException(ErrorCode.INVALID_TOKEN)
        }

        // DB 토큰 조회
        val storedToken =
            refreshTokenRepository.findByToken(refreshToken) ?: throw CustomException(ErrorCode.INVALID_TOKEN)
        val jwtMemberId = jwtUtil.getMemberId(refreshToken) ?: throw CustomException(ErrorCode.INVALID_TOKEN)

        if (storedToken.member.id != jwtMemberId) {
            refreshTokenRepository.delete(storedToken)
            throw CustomException(ErrorCode.INVALID_TOKEN)
        }

        // 토큰 만료 재검증
        if (storedToken.expiresAt.isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(storedToken)
            throw CustomException(ErrorCode.EXPIRED_TOKEN)
        }

        val member = storedToken.member

        val newAccessToken = jwtUtil.createAccessToken(member.id, member.role.toString())
        val newRefreshToken = jwtUtil.createRefreshToken(member.id)

        val refreshTokenEntity = RefreshToken(
            member, newRefreshToken, LocalDateTime.now().plusDays(1)
        )

        refreshTokenRepository.delete(storedToken)
        refreshTokenRepository.flush()
        refreshTokenRepository.save(refreshTokenEntity)

        return TokenResponse(newAccessToken, newRefreshToken)
    }

    fun revokeRefreshToken(refreshToken: String) {
        refreshTokenRepository.deleteByToken(refreshToken)
    }
}