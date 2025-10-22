package com.perflog.domain.member.repository

import com.perflog.domain.member.model.RefreshToken
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun findByToken(token: String): RefreshToken?

    @Modifying
    @Transactional
    @Query("DELETE FROM RefreshToken rt WHERE rt.member.id = :memberId")
    fun deleteAllByMemberId(memberId: Long)
}