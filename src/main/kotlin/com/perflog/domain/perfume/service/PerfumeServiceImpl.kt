package com.perflog.domain.perfume.service

import com.perflog.common.error.CustomException
import com.perflog.common.error.ErrorCode
import com.perflog.domain.member.model.MemberRole
import com.perflog.domain.member.repository.MemberRepository
import com.perflog.domain.perfume.dto.PerfumeDto
import com.perflog.domain.perfume.model.entity.Perfume
import com.perflog.domain.perfume.model.entity.PerfumeTag
import com.perflog.domain.perfume.repository.PerfumeRepository
import com.perflog.domain.perfume.repository.PerfumeTagRepository
import com.perflog.domain.perfume.repository.TagRepository
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class PerfumeServiceImpl(
    private val perfumeRepository: PerfumeRepository,
    private val tagRepository: TagRepository,
    private val perfumeTagRepository: PerfumeTagRepository,
    private val memberRepository: MemberRepository
) : PerfumeService {

    @Transactional
    override fun createPerfume(request: PerfumeDto.PerfumeRequest) {
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
        requireAdmin(authentication)

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

        return PerfumeDto.PerfumeResponse.from(perfume, tags)
    }

    @Transactional
    override fun deletePerfume(
        id: Long,
        authentication: Authentication
    ) {
        requireAdmin(authentication)

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

    override fun getPerfumeList(): PerfumeDto.PerfumeListResponse {
        val perfumes = perfumeRepository.findAll()

        val items = perfumes.map {
            PerfumeDto.PerfumeListResponse.PerfumeSimple(
                id = it.id,
                name = it.name,
                brand = it.brand,
                season = it.season.description,
                gender = it.gender.description
            )
        }

        return PerfumeDto.PerfumeListResponse(items = items)
    }

    private fun requireAdmin(authentication: Authentication) {
        val email = authentication.name
        val member = (memberRepository.findByEmail(email)
            ?: throw CustomException(ErrorCode.MEMBER_NOT_FOUND))

        if (member.role != MemberRole.ROLE_ADMIN) {
            throw CustomException(ErrorCode.FORBIDDEN)
        }
    }
}