package com.perflog.domain.search.service

import com.perflog.domain.perfume.dto.PerfumeDto
import com.perflog.domain.perfume.model.document.PerfumeDocument
import com.perflog.domain.perfume.model.enum.SearchTarget
import com.perflog.domain.perfume.repository.PerfumeSearchRepository
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SearchServiceImpl(
    private val perfumeSearchRepository: PerfumeSearchRepository,
    private val elasticsearchOperations: ElasticsearchOperations
) : SearchService {

    override fun searchPerfume(
        keyword: String,
        target: SearchTarget
    ): List<PerfumeDocument> {
        return when (target) {
            SearchTarget.NAME -> perfumeSearchRepository.findByNameContainingIgnoreCase(keyword)
            SearchTarget.BRAND -> perfumeSearchRepository.findByBrandContainingIgnoreCase(keyword)
            SearchTarget.NOTES -> perfumeSearchRepository.findByNotesContainingIgnoreCase(keyword)
            SearchTarget.ALL -> perfumeSearchRepository.findByNameContainingIgnoreCaseOrBrandContainingIgnoreCaseOrNotesContainingIgnoreCase(
                keyword, keyword, keyword
            )
        }
    }

    override fun autocomplete(keyword: String): List<PerfumeDto.autocomplete> {
        try {

            val query = NativeQuery.builder()
                .withQuery { q ->
                    q.match { m ->
                        m.field("name")
                            .query(keyword)
                    }
                }
                .withMaxResults(5)
                .build()

            val searchHits =
                elasticsearchOperations.search(query, PerfumeDocument::class.java)
            return searchHits.searchHits
                .map { PerfumeDto.autocomplete(name = it.content.name) }

        } catch (e: Exception) {
            println(e.message)
        }
        return emptyList()
    }
}
