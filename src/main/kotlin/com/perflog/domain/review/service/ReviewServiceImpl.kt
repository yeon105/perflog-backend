package com.perflog.domain.review.service

import com.perflog.common.error.CustomException
import com.perflog.common.error.ErrorCode
import com.perflog.domain.member.repository.MemberRepository
import com.perflog.domain.perfume.repository.PerfumeRepository
import com.perflog.domain.review.dto.ReviewDto
import com.perflog.domain.review.model.Review
import com.perflog.domain.review.repository.ReviewRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class ReviewServiceImpl(
    private val reviewRepository: ReviewRepository,
    private val memberRepository: MemberRepository,
    private val perfumeRepository: PerfumeRepository,
) : ReviewService {

    override fun createReview(request: ReviewDto.ReviewRequest, authentication: Authentication) {
        val member = memberRepository.findByEmail(authentication.name)
            ?: throw CustomException(ErrorCode.MEMBER_NOT_FOUND)

        val perfume = perfumeRepository.findById(request.perfumeId)
            .orElseThrow { CustomException(ErrorCode.PERFUME_NOT_FOUND) }

        if (reviewRepository.existsByMemberAndPerfume(member, perfume)) {
            throw CustomException(ErrorCode.REVIEW_ALREADY_EXISTS)
        }

        if (request.rating !in 1..5) {
            throw CustomException(ErrorCode.INVALID_RATING)
        }

        val review = Review(
            member = member,
            perfume = perfume,
            rating = request.rating,
            content = request.content
        )

        reviewRepository.save(review)
    }

    override fun updateReview(
        id: Long,
        request: ReviewDto.ReviewRequest,
        authentication: Authentication
    ): ReviewDto.ReviewResponse {
        val review = reviewRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.REVIEW_NOT_FOUND) }

        val currentEmail = authentication.name
        val authorEmail = review.member.email
        if (authorEmail != currentEmail) {
            throw CustomException(ErrorCode.FORBIDDEN)
        }

        review.rating = request.rating
        review.content = request.content

        val savedReview = reviewRepository.save(review)

        return ReviewDto.ReviewResponse(
            id = savedReview.id,
            perfumeId = savedReview.perfume.id,
            rating = savedReview.rating,
            content = savedReview.content,
            createdAt = savedReview.createdAt,
            updatedAt = savedReview.updatedAt
        )
    }

    override fun deleteReview(id: Long, authentication: Authentication) {
        val review = reviewRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.REVIEW_NOT_FOUND) }

        val currentEmail = authentication.name.lowercase()
        val authorEmail = review.member.email.lowercase()
        val authorities = authentication.authorities.map { it.authority }
        
        if (authorEmail != currentEmail && "ROLE_ADMIN" !in authorities) {
            throw CustomException(ErrorCode.FORBIDDEN)
        }

        reviewRepository.delete(review)
    }

    @Transactional(readOnly = true)
    override fun getReviewsByPerfumeId(perfumeId: Long): List<ReviewDto.ReviewResponse> {
        val reviews = reviewRepository.findByPerfumeId(perfumeId)
        if (reviews.isEmpty()) {
            throw CustomException(ErrorCode.REVIEW_NOT_FOUND)
        }

        return reviews.map { review ->
            ReviewDto.ReviewResponse(
                id = review.id,
                perfumeId = review.perfume.id,
                rating = review.rating,
                content = review.content,
                createdAt = review.createdAt,
                updatedAt = review.updatedAt
            )
        }
    }

    @Transactional(readOnly = true)
    override fun getSummary(perfumeId: Long): ReviewDto.Summary {
        val reviews = reviewRepository.findByPerfumeId(perfumeId)
        if (reviews.isEmpty()) {
            throw CustomException(ErrorCode.REVIEW_NOT_FOUND)
        }

        val avg = reviews.map { it.rating }.average()
        val distribution = reviews.groupingBy { it.rating }.eachCount()

        return ReviewDto.Summary(
            perfumeId = perfumeId,
            averageRating = avg,
            ratingDistribution = distribution
        )
    }
}