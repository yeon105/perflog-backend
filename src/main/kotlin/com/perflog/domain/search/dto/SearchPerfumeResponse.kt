package com.perflog.domain.search.dto

import com.perflog.domain.perfume.model.document.PerfumeDocument
import org.springframework.data.elasticsearch.core.SearchHit

data class SearchPerfumeResponse(
    val id: String,
    val name: String,
    val brand: String,
    val launchYear: Int?,
    val season: String?,
    val gender: String?,
    val notes: String?,
    val tags: List<String>,
) {
    companion object {
        fun from(hit: SearchHit<PerfumeDocument>): SearchPerfumeResponse {
            val doc = hit.content

            return SearchPerfumeResponse(
                id = doc.id,
                name = doc.name,
                brand = doc.brand,
                launchYear = doc.launchYear,
                season = doc.season,
                gender = doc.gender,
                notes = doc.notes,
                tags = doc.tags,
            )
        }

    }
}
