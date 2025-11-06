package com.perflog.domain.preference.service

import com.perflog.common.error.CustomException
import com.perflog.common.error.ErrorCode
import com.perflog.domain.member.model.Member
import com.perflog.domain.member.repository.MemberRepository
import com.perflog.domain.perfume.dto.PerfumeDto
import com.perflog.domain.perfume.dto.PerfumeReviewSummaryDto
import com.perflog.domain.perfume.repository.PerfumeRepository
import com.perflog.domain.perfume.repository.PerfumeTagRepository
import com.perflog.domain.preference.dto.PreferenceDto
import com.perflog.domain.preference.model.PerfumePreference
import com.perflog.domain.preference.repository.PerfumePreferenceRepository
import com.perflog.domain.review.repository.ReviewRepository
import jakarta.transaction.Transactional
import org.springframework.data.domain.PageRequest
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
@Transactional
class PerfumePreferenceServiceImpl(
    private val preferenceRepository: PerfumePreferenceRepository,
    private val memberRepository: MemberRepository,
    private val perfumeRepository: PerfumeRepository,
    private val perfumeTagRepository: PerfumeTagRepository,
    private val reviewRepository: ReviewRepository
) : PerfumePreferenceService {

    override fun recordPerfumePreference(
        id: Long,
        request: PreferenceDto.CreateRequest,
        authentication: Authentication
    ) {
        val member = findMember(authentication)

        val perfume = perfumeRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.PERFUME_NOT_FOUND) }

        val perfumePreference = PerfumePreference(
            member = member,
            perfume = perfume,
            status = request.status,
            usedAt = request.usedAt
                ?: throw CustomException(ErrorCode.USED_AT_REQUIRED)
        )

        preferenceRepository.save(perfumePreference)
    }

    override fun getPreferredPerfumes(authentication: Authentication): List<PerfumeDto.PerfumeSimpleResponse> {
        val member = findMember(authentication)

        val oneMonthAgo = LocalDate.now().minusMonths(1)
        val perfumeIds = preferenceRepository.findRecentLikedPerfumeIds(member.id, oneMonthAgo)
        if (perfumeIds.isEmpty()) {
            return emptyList()
        }

        val topTagIds = perfumeTagRepository.findTopTagsByPerfumeIds(perfumeIds, PageRequest.of(0, 3))

        val perfumes = perfumeTagRepository.findPerfumesByMatchingTags(topTagIds)
        return perfumes.map { perfume ->
            val summary = reviewRepository.findSummaryByPerfumeId(perfume.id)
                ?: PerfumeReviewSummaryDto(0.0, 0)

            PerfumeDto.PerfumeSimpleResponse(
                id = perfume.id,
                name = perfume.name,
                brand = perfume.brand,
                season = perfume.season.description,
                gender = perfume.gender.description,
                imageUrl = perfume.imageUrl,
                averageRating = summary.averageRating,
                reviewCount = summary.reviewCount
            )
        }
    }

    private fun findMember(authentication: Authentication): Member {
        val email = authentication.name
        return memberRepository.findByEmail(email)
            ?: throw CustomException(ErrorCode.MEMBER_NOT_FOUND)
    }
}