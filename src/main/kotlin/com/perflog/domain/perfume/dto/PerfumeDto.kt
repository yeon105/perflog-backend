package com.perflog.domain.perfume.dto

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
    )

    // 향수 목록 응답 DTO
    data class PerfumeListResponse(
        val items: List<PerfumeSimple>
    ) {
        // 목록용 간단 향수 정보 DTO
        data class PerfumeSimple(
            val id: Long,
            val name: String,
            val brand: String,
            val season: String,
            val gender: String
        )
    }
}