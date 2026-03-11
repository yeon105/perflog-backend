package com.perflog.config.Kafka

import com.perflog.domain.perfume.model.entity.Perfume
import com.perflog.domain.perfume.model.entity.Tag

data class PerfumeCreatedEvent(
    val id: Long,
    val name: String,
    val brand: String,
    val launchYear: Int,
    val season: String,
    val gender: String,
    val notes: String,
    val tags: List<String>
) {
    companion object {

        fun of(
            perfume: Perfume,
            tags: List<Tag>
        ): PerfumeCreatedEvent {

            return PerfumeCreatedEvent(
                id = perfume.id,
                name = perfume.name,
                brand = perfume.brand,
                launchYear = perfume.launchYear,
                season = perfume.season.name,
                gender = perfume.gender.name,
                notes = listOfNotNull(
                    perfume.topNotes,
                    perfume.middleNotes,
                    perfume.baseNotes
                ).joinToString(" "),
                tags = tags.map { it.name }
            )
        }
    }
}