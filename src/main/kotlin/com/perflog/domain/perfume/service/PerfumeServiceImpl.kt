package com.perflog.domain.perfume.service

import com.perflog.common.dto.Paging
import com.perflog.common.error.CustomException
import com.perflog.common.error.ErrorCode
import com.perflog.config.Kafka.PerfumeCreatedEvent
import com.perflog.config.Kafka.PerfumeEventProducer
import com.perflog.domain.member.repository.MemberRepository
import com.perflog.domain.perfume.dto.PerfumeDto
import com.perflog.domain.perfume.model.entity.Perfume
import com.perflog.domain.perfume.model.entity.PerfumeTag
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
    private val perfumeEventProducer: PerfumeEventProducer
) : PerfumeService {

    @Transactional
    override fun createPerfume(request: PerfumeDto.PerfumeRequest, authentication: Authentication) {
        findMember(authentication)

        if (perfumeRepository.existsByNameAndBrand(request.name, request.brand)) {
            throw CustomException(ErrorCode.DUPLICATE_PERFUME)
        }
        val tags = tagRepository.findAllById(request.tagIds.toSet())

        if (tags.size != request.tagIds.size) {
            throw CustomException(ErrorCode.TAG_NOT_FOUND)
        }
        val perfume =
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
        
        tags.forEach { perfume.addTag(it) }

        perfumeRepository.save(perfume)

        //        엘라스틱 서치 저장
        perfumeEventProducer.sendCreatedEvent(
            PerfumeCreatedEvent.of(perfume, tags)
        )
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

        if (tagIds.isNotEmpty()) {
            val newLinks = tagIds.map { tagId ->
                PerfumeTag(perfume = perfume, tag = tagsById.getValue(tagId))
            }
            perfumeTagRepository.saveAll(newLinks)
        }

        val tags = perfumeTagRepository.findByPerfume(perfume).map { it.tag.name }

        return PerfumeDto.PerfumeResponse.from(perfume, tags)
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

        return PerfumeDto.PerfumeResponse.from(perfume, tags)
    }

    override fun getPerfumeList(requestDto: Paging.PageRequestDto): Paging.PageResponseDto<PerfumeDto.PerfumeSimpleResponse> {
        val pageable = requestDto.toPageable()
        val page = perfumeRepository.findAll(pageable)

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
}