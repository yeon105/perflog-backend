package com.perflog.domain.preference.repository

import com.perflog.domain.preference.model.PerfumePreference
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface PerfumePreferenceRepository : JpaRepository<PerfumePreference, Long> {

    @Query(
        """
    select distinct pp.perfume.id
    from PerfumePreference pp
    where pp.member.id = :memberId
      and pp.status = 'LIKE'
      and pp.usedAt >= :oneMonthAgo
"""
    )
    fun findRecentLikedPerfumeIds(memberId: Long, oneMonthAgo: LocalDate): List<Long>

    fun existsByMemberIdAndPerfumeId(memberId: Long, perfumeId: Long): Boolean
}