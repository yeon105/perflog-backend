package com.perflog.domain.perfume.model.document

import com.perflog.domain.perfume.model.entity.Perfume

data class PerfumeDocument(
    val id: Long,
    val name: String,
    val brand: String,
    val season: String,
    val gender: String,
    val imageUrl: String,
) {

    companion object {

        fun from(perfume: Perfume): PerfumeDocument {
            return PerfumeDocument(
                id = perfume.id,
                name = perfume.name,
                brand = perfume.brand,
                season = perfume.season.name,
                gender = perfume.gender.name,
                imageUrl = perfume.imageUrl,

                )
        }
    }
}
