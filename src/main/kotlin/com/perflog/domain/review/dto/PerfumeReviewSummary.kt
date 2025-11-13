package com.perflog.domain.review.dto

interface PerfumeReviewSummary {
    val perfumeId: Long
    val averageRating: Double?
    val reviewCount: Long
}