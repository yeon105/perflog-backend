package com.perflog.domain.perfume.service

import com.perflog.common.dto.Paging
import com.perflog.common.error.CustomException
import com.perflog.common.error.ErrorCode
import com.perflog.domain.member.repository.MemberRepository
import com.perflog.domain.perfume.dto.PerfumeDto
import com.perflog.domain.perfume.model.entity.Perfume
import com.perflog.domain.perfume.model.entity.PerfumeTag
import com.perflog.domain.perfume.model.enum.SearchTarget
import com.perflog.domain.perfume.repository.PerfumeRepository
import com.perflog.domain.perfume.repository.PerfumeTagRepository
import com.perflog.domain.perfume.repository.TagRepository
import com.perflog.domain.review.dto.PerfumeReviewSummary
import com.perflog.domain.review.repository.ReviewRepository
import org.springframework.data.domain.Page
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class PerfumeServiceImpl(
    private val perfumeRepository: PerfumeRepository,
    private val tagRepository: TagRepository,
    private val perfumeTagRepository: PerfumeTagRepository,
    private val memberRepository: MemberRepository,
    private val reviewRepository: ReviewRepository,
) : PerfumeService {

    @Transactional
    override fun createPerfume(request: PerfumeDto.PerfumeRequest, authentication: Authentication) {
        findMember(authentication)

        if (perfumeRepository.existsByNameAndBrand(request.name, request.brand)) {
            throw CustomException(ErrorCode.DUPLICATE_PERFUME)
        }

        val tagIds = request.tagIds.toSet()
        val tagsById = tagRepository.findAllById(tagIds).associateBy { it.id }

        if (tagsById.size != tagIds.size) {
            val missing = tagIds - tagsById.keys
            if (missing.isNotEmpty()) {
                throw CustomException(ErrorCode.TAG_NOT_FOUND)
            }
        }

        val perfume = perfumeRepository.save(
            Perfume(
                name = request.name,
                brand = request.brand,
                launchYear = request.launchYear,
                imageUrl = request.imageUrl,
                longevity = request.longevity,
                season = request.season,
                gender = request.gender,
                topNotes = request.topNotes.joinToString(",").ifBlank { null },
                middleNotes = request.middleNotes.joinToString(",").ifBlank { null },
                baseNotes = request.baseNotes.joinToString(",").ifBlank { null }
            )
        )

        val tagLinks = tagIds.map { tagId ->
            PerfumeTag(
                perfume = perfume,
                tag = tagsById.getValue(tagId)
            )
        }

        perfumeTagRepository.saveAll(tagLinks)
    }

    @Transactional
    override fun updatePerfume(
        id: Long,
        request: PerfumeDto.PerfumeRequest,
        authentication: Authentication
    ): PerfumeDto.PerfumeResponse {
        findMember(authentication)

        val perfume = perfumeRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.PERFUME_NOT_FOUND) }

        val tagIds = request.tagIds.toSet()
        val tagsById = tagRepository.findAllById(tagIds).associateBy { it.id }
        if (tagsById.size != tagIds.size) {
            val missing = tagIds - tagsById.keys
            if (missing.isNotEmpty()) {
                throw CustomException(ErrorCode.TAG_NOT_FOUND)
            }
        }

        perfume.apply {
            name = request.name
            brand = request.brand
            launchYear = request.launchYear
            imageUrl = request.imageUrl
            longevity = request.longevity
            season = request.season
            gender = request.gender
            topNotes = request.topNotes.joinToString(",").ifBlank { null }
            middleNotes = request.middleNotes.joinToString(",").ifBlank { null }
            baseNotes = request.baseNotes.joinToString(",").ifBlank { null }
        }

        perfumeTagRepository.deleteByPerfumeId(id)
        if (tagIds.isNotEmpty()) {
            val newLinks = tagIds.map { tagId ->
                PerfumeTag(perfume = perfume, tag = tagsById.getValue(tagId))
            }
            perfumeTagRepository.saveAll(newLinks)
        }

        val tags = perfumeTagRepository.findByPerfume(perfume).map { it.tag.name }

        return PerfumeDto.PerfumeResponse(
            id = perfume.id,
            name = perfume.name,
            brand = perfume.brand,
            launchYear = perfume.launchYear,
            imageUrl = perfume.imageUrl,
            longevity = perfume.longevity.description,
            season = perfume.season.description,
            gender = perfume.gender.description,
            topNotes = splitNotes(perfume.topNotes),
            middleNotes = splitNotes(perfume.middleNotes),
            baseNotes = splitNotes(perfume.baseNotes),
            tags = tags
        )
    }

    @Transactional
    override fun deletePerfume(
        id: Long,
        authentication: Authentication
    ) {
        findMember(authentication)

        val perfume = perfumeRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.PERFUME_NOT_FOUND) }

        perfumeRepository.delete(perfume)
    }

    override fun getPerfume(id: Long): PerfumeDto.PerfumeResponse {
        val perfume = perfumeRepository.findWithTagsById(id)
            .orElseThrow { CustomException(ErrorCode.PERFUME_NOT_FOUND) }

        val tags = perfume.perfumeTags.map { it.tag.name }

        return PerfumeDto.PerfumeResponse(
            id = perfume.id,
            name = perfume.name,
            brand = perfume.brand,
            launchYear = perfume.launchYear,
            imageUrl = perfume.imageUrl,
            longevity = perfume.longevity.description,
            season = perfume.season.description,
            gender = perfume.gender.description,
            topNotes = splitNotes(perfume.topNotes),
            middleNotes = splitNotes(perfume.middleNotes),
            baseNotes = splitNotes(perfume.baseNotes),
            tags = tags
        )
    }

    override fun getPerfumeList(requestDto: Paging.PageRequestDto): Paging.PageResponseDto<PerfumeDto.PerfumeSimpleResponse> {
        val pageable = requestDto.toPageable()
        val page = perfumeRepository.findAll(pageable)

        return toPageResponse(page)
    }


    override fun searchPerfume(
        target: SearchTarget,
        keyword: String,
        requestDto: Paging.PageRequestDto
    ): Paging.PageResponseDto<PerfumeDto.PerfumeSimpleResponse> {
        val pageable = requestDto.toPageable()
        val page = when (target) {
            SearchTarget.NAME -> perfumeRepository.findByNameContainingIgnoreCase(keyword, pageable)
            SearchTarget.BRAND -> perfumeRepository.findByBrandContainingIgnoreCase(keyword, pageable)
            SearchTarget.ALL -> perfumeRepository.findByNameContainingIgnoreCaseOrBrandContainingIgnoreCase(
                keyword,
                keyword,
                pageable
            )
        }

        return toPageResponse(page)
    }

    private fun toPageResponse(
        page: Page<Perfume>
    ): Paging.PageResponseDto<PerfumeDto.PerfumeSimpleResponse> {
        val perfumeIds = page.content.map { it.id }

        val summaries: Map<Long, PerfumeReviewSummary> =
            reviewRepository.findSummariesByPerfumeIds(perfumeIds)
                .associateBy { it.perfumeId }

        val items = page.content.map { perfume ->
            val summary = summaries[perfume.id]
            PerfumeDto.PerfumeSimpleResponse(
                id = perfume.id,
                name = perfume.name,
                brand = perfume.brand,
                season = perfume.season.description,
                gender = perfume.gender.description,
                imageUrl = perfume.imageUrl,
                averageRating = summary?.averageRating ?: 0.0,
                reviewCount = summary?.reviewCount ?: 0L
            )
        }

        val meta = Paging.PageMeta(
            page = page.number + 1,
            size = page.size,
            totalElements = page.totalElements,
            totalPages = page.totalPages,
            hasNext = page.hasNext(),
            hasPrev = page.hasPrevious()
        )

        return Paging.PageResponseDto(items, meta)
    }

    private fun findMember(authentication: Authentication) {
        val email = authentication.name
        memberRepository.findByEmail(email)
            ?: throw CustomException(ErrorCode.MEMBER_NOT_FOUND)
    }

    private fun splitNotes(s: String?): List<String> = s?.split(",") ?: emptyList()
}