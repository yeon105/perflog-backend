package com.perflog.domain.search.service

import co.elastic.clients.elasticsearch.ElasticsearchClient
import com.perflog.domain.perfume.model.entity.PerfumeDocument
import com.perflog.domain.perfume.model.enum.SearchTarget
import com.perflog.domain.perfume.repository.PerfumeRepository
import com.perflog.domain.perfume.repository.PerfumeSearchRepository
import org.springframework.stereotype.Service

@Service
class SearchServiceImpl(
    private val esClient: ElasticsearchClient,
    private val perfumeRepository: PerfumeRepository,
    private val perfumeSearchRepository: PerfumeSearchRepository
) : SearchService {

    override fun searchPerfume(
        target: SearchTarget,
        keyword: String
    ): List<PerfumeDocument> {
        return when (target) {
            SearchTarget.NAME ->
                perfumeSearchRepository.findByNameContaining(keyword)

            SearchTarget.BRAND ->
                perfumeSearchRepository.findByBrandContaining(keyword)

            SearchTarget.ALL ->
                perfumeSearchRepository
                    .findByNameContainingAndBrandContaining(keyword, keyword)

        }

    }


    override fun reindexAll(): String {
        val start = System.currentTimeMillis()

        val perfumes = perfumeRepository.findAll() // 기존 DB 엔티티
        val documents = perfumes.map { entity ->
            PerfumeDocument(
                id = entity.id.toString(),
                name = entity.name,
                brand = entity.brand
            )
        }

        perfumeSearchRepository.saveAll(documents)

        val end = System.currentTimeMillis()
        return "Reindex 완료: ${documents.size}건, 소요시간 ${end - start}ms"
    }
}