package com.perflog.domain.review.repository

import com.perflog.domain.member.model.Member
import com.perflog.domain.perfume.model.entity.Perfume
import com.perflog.domain.review.model.Review
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository : JpaRepository<Review, Long> {
    fun existsByMemberAndPerfume(member: Member, perfume: Perfume): Boolean
    fun findByPerfumeId(perfumeId: Long): List<Review>
}