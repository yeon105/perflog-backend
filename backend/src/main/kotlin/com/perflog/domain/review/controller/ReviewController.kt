package com.perflog.domain.review.controller

import com.perflog.domain.review.dto.ReviewDto
import com.perflog.domain.review.service.ReviewService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reviews")
class ReviewController(
    private val reviewService: ReviewService
) {

    @PostMapping
    fun createReview(
        @RequestBody request: ReviewDto.ReviewRequest,
        authentication: Authentication
    ): ResponseEntity<Void> {
        reviewService.createReview(request, authentication)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PutMapping("/{reviewId}")
    fun updateReview(
        @PathVariable reviewId: Long,
        @RequestBody request: ReviewDto.ReviewRequest, authentication: Authentication
    ): ResponseEntity<ReviewDto.ReviewResponse> {
        val response = reviewService.updateReview(reviewId, request, authentication)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{reviewId}")
    fun deleteReview(
        @PathVariable reviewId: Long,
        authentication: Authentication
    ): ResponseEntity<Void> {
        reviewService.deleteReview(reviewId, authentication)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/perfume/{perfumeId}")
    fun getReviewsByPerfume(@PathVariable perfumeId: Long): ResponseEntity<List<ReviewDto.ReviewResponse>> {
        val reviews = reviewService.getReviewsByPerfumeId(perfumeId)
        return ResponseEntity.ok(reviews)
    }

    @GetMapping("/perfume/{perfumeId}/summary")
    fun getReviewSummary(@PathVariable perfumeId: Long): ResponseEntity<ReviewDto.Summary> {
        val summary = reviewService.getSummary(perfumeId)
        return ResponseEntity.ok(summary)
    }
}