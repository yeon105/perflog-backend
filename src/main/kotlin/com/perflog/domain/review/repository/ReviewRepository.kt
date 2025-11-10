package com.perflog.domain.review.repository

import com.perflog.domain.member.model.Member
import com.perflog.domain.perfume.dto.PerfumeReviewSummaryDto
import com.perflog.domain.perfume.model.entity.Perfume
import com.perflog.domain.review.model.Review
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ReviewRepository : JpaRepository<Review, Long> {
    fun existsByMemberAndPerfume(member: Member, perfume: Perfume): Boolean
    fun findByPerfumeId(perfumeId: Long): List<Review>

    @Query(
        """
        select new com.perflog.domain.perfume.dto.PerfumeReviewSummaryDto(
            AVG(r.rating),
            COUNT(r.id)
        )
        from Review r
        where r.perfume.id = :perfumeId
        group by r.perfume.id
    """
    )
    fun findSummaryByPerfumeId(@Param("perfumeId") perfumeId: Long): PerfumeReviewSummaryDto?

    @Query("select r from Review r where r.member.id = :memberId")
    fun findAllByMemberId(memberId: Long, pageable: Pageable): Page<Review>

}