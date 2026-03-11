package com.perflog.domain.perfume.dto

import com.perflog.domain.perfume.model.entity.Perfume
import com.perflog.domain.perfume.model.enum.Gender
import com.perflog.domain.perfume.model.enum.Longevity
import com.perflog.domain.perfume.model.enum.Season

class PerfumeDto {

    // 향수 등록 요청 DTO
    data class PerfumeRequest(
        val name: String,
        val brand: String,
        val launchYear: Int,
        val imageUrl: String,
        val longevity: Longevity,
        val season: Season,
        val gender: Gender,
        val topNotes: List<String> = emptyList(),
        val middleNotes: List<String> = emptyList(),
        val baseNotes: List<String> = emptyList(),
        val tagIds: List<Long> = emptyList()
    )

    // 향수 상세 응답 DTO
    data class PerfumeResponse(
        val id: Long,
        val name: String,
        val brand: String,
        val launchYear: Int,
        val imageUrl: String,
        val longevity: String,
        val season: String,
        val gender: String,
        val topNotes: List<String>,
        val middleNotes: List<String>,
        val baseNotes: List<String>,
        val tags: List<String>
    ) {
        companion object {
            fun from(perfume: Perfume, tags: List<String>): PerfumeResponse {
                return PerfumeResponse(
                    id = perfume.id,
                    name = perfume.name,
                    brand = perfume.brand,
                    launchYear = perfume.launchYear,
                    imageUrl = perfume.imageUrl,
                    longevity = perfume.longevity.description,
                    season = perfume.season.description,
                    gender = perfume.gender.description,
                    topNotes = split(perfume.topNotes),
                    middleNotes = split(perfume.middleNotes),
                    baseNotes = split(perfume.baseNotes),
                    tags = tags
                )
            }

            private fun split(notes: String?) =
                notes?.split(",")?.map { it.trim() } ?: emptyList()
        }
    }

    // 향수 간단 응답 DTO
    data class PerfumeSimpleResponse(
        val id: Long,
        val name: String,
        val brand: String,
        val season: String,
        val gender: String,
        val imageUrl: String,
        val averageRating: Double,
        val reviewCount: Long
    )

    data class autocomplete(
        val name: String,
    )
}