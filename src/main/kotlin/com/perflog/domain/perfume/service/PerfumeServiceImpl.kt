package com.perflog.domain.perfume.service

import com.perflog.common.error.CustomException
import com.perflog.common.error.ErrorCode
import com.perflog.domain.perfume.dto.PerfumeDto
import com.perflog.domain.perfume.model.entity.Perfume
import com.perflog.domain.perfume.model.entity.PerfumeTag
import com.perflog.domain.perfume.repository.PerfumeRepository
import com.perflog.domain.perfume.repository.PerfumeTagRepository
import com.perflog.domain.perfume.repository.TagRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional(readOnly = true)
@Service
class PerfumeServiceImpl(
    private val perfumeRepository: PerfumeRepository,
    private val tagRepository: TagRepository,
    private val perfumeTagRepository: PerfumeTagRepository
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

    override fun getPerfume(id: Long): PerfumeDto.PerfumeResponse {
        val perfume = perfumeRepository.findById(id)
            .orElseThrow { CustomException(ErrorCode.PERFUME_NOT_FOUND) }

        val tags = perfumeTagRepository.findByPerfume(perfume).map { it.tag.name }

        fun splitNotes(s: String?): List<String> = s?.split(",") ?: emptyList()

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

}