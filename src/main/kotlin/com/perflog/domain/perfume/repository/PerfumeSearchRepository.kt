package com.perflog.domain.perfume.repository

import com.perflog.domain.perfume.model.document.PerfumeDocument
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface PerfumeSearchRepository : ElasticsearchRepository<PerfumeDocument, String> {
    fun findByNameContainingIgnoreCase(name: String): List<PerfumeDocument>

    fun findByBrandContainingIgnoreCase(brand: String): List<PerfumeDocument>

    fun findByNotesContainingIgnoreCase(notes: String): List<PerfumeDocument>

    fun findByNameContainingIgnoreCaseOrBrandContainingIgnoreCaseOrNotesContainingIgnoreCase(
        name: String,
        brand: String,
        notes: String
    ): List<PerfumeDocument>
}
