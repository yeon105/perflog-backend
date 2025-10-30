package com.perflog.domain.review.dto

import java.time.LocalDateTime

class ReviewDto {
    // 리뷰 생성/수정 요청 DTO
    data class ReviewRequest(
        val perfumeId: Long,
        val rating: Int,
        val content: String
    )

    // 리뷰 응답 DTO
    data class ReviewResponse(
        val id: Long,
        val perfumeId: Long,
        val rating: Int,
        val content: String,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
    )

    // 향수별 리뷰 통계
    data class Summary(
        val perfumeId: Long,
        val averageRating: Double, // 평균 평점
        val ratingDistribution: Map<Int, Int> // 평점별 개수 분포
    )
}