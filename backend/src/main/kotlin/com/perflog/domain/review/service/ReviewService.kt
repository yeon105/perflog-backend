package com.perflog.domain.review.service

import com.perflog.domain.review.dto.ReviewDto
import org.springframework.security.core.Authentication

interface ReviewService {

    /**
     * 새로운 리뷰를 등록한다.
     *
     * @param request 리뷰 생성 요청 DTO
     */
    fun createReview(request: ReviewDto.ReviewRequest, authentication: Authentication)

    /**
     * 특정 리뷰를 수정한다.
     *
     * @param id 리뷰 ID
     * @param request 리뷰 수정 요청 DTO
     * @param authentication 현재 로그인한 사용자 정보 (작성자 검증용)
     * @return 수정된 리뷰 응답 DTO
     */
    fun updateReview(
        id: Long,
        request: ReviewDto.ReviewRequest,
        authentication: Authentication
    ): ReviewDto.ReviewResponse

    /**
     * 특정 리뷰를 삭제한다.
     *
     * @param id 리뷰 ID
     * @param authentication 현재 로그인한 사용자 정보 (작성자 검증용)
     */
    fun deleteReview(id: Long, authentication: Authentication)

    /**
     * 특정 향수에 작성된 리뷰 목록을 조회한다.
     *
     * @param perfumeId 향수 ID
     * @return 리뷰 응답 DTO 목록
     */
    fun getReviewsByPerfumeId(perfumeId: Long): List<ReviewDto.ReviewResponse>

    /**
     * 특정 향수에 대한 리뷰 요약 정보를 조회한다.
     * (평균 평점, 평점 분포 등)
     *
     * @param perfumeId 향수 ID
     * @return 리뷰 요약 응답 DTO
     */
    fun getSummary(perfumeId: Long): ReviewDto.Summary
}