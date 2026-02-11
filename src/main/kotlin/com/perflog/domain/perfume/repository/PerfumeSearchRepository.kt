package com.perflog.domain.perfume.repository

import com.perflog.domain.perfume.model.entity.PerfumeDocument
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface PerfumeSearchRepository : ElasticsearchRepository<PerfumeDocument, Long> {

    fun findByNameContaining(keyword: String): List<PerfumeDocument>

    fun findByBrandContaining(keyword: String): List<PerfumeDocument>

    fun findByNameContainingAndBrandContaining(
        name: String,
        brand: String
    ): List<PerfumeDocument>
}