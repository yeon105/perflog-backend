package com.perflog.domain.review.repository

import com.perflog.domain.member.model.Member
import com.perflog.domain.perfume.model.entity.Perfume
import com.perflog.domain.review.dto.PerfumeReviewSummary
import com.perflog.domain.review.model.Review
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ReviewRepository : JpaRepository<Review, Long> {
    fun existsByMemberAndPerfume(member: Member, perfume: Perfume): Boolean
    fun findByPerfumeId(perfumeId: Long): List<Review>

    @Query(
        """
    select r.perfume.id as perfumeId, avg(r.rating) as averageRating, count(r.id) as reviewCount
    from Review r
    where r.perfume.id in :perfumeIds
    group by r.perfume.id
"""
    )
    fun findSummariesByPerfumeIds(@Param("perfumeIds") perfumeIds: List<Long>): List<PerfumeReviewSummary>
}