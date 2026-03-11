package com.perflog.domain.search.service

import com.perflog.domain.perfume.dto.PerfumeDto
import com.perflog.domain.perfume.model.document.PerfumeDocument
import com.perflog.domain.perfume.model.enum.SearchTarget
import com.perflog.domain.perfume.repository.PerfumeSearchRepository
import com.perflog.domain.search.dto.SearchPerfumeResponse
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
    ): List<SearchPerfumeResponse> {

        val query = NativeQuery.builder()
            .withQuery { q ->
                q.bool { b ->

                    when (target) {

                        SearchTarget.NAME ->
                            b.must {
                                it.match { m ->
                                    m.field("name")
                                        .query(keyword)
                                }
                            }

                        SearchTarget.BRAND ->
                            b.must {
                                it.match { m ->
                                    m.field("brand")
                                        .query(keyword)
                                }
                            }

                        SearchTarget.NOTES ->
                            b.must {
                                it.match { m ->
                                    m.field("notes")
                                        .query(keyword)
                                }
                            }

                        SearchTarget.ALL ->
                            b.should {
                                it.match { m ->
                                    m.field("name")
                                        .query(keyword)
                                        .boost(3.0f)
                                }
                            }
                                .should {
                                    it.match { m ->
                                        m.field("brand")
                                            .query(keyword)
                                            .boost(2.0f)
                                    }
                                }
                                .should {
                                    it.match { m ->
                                        m.field("notes")
                                            .query(keyword)
                                    }
                                }
                                .minimumShouldMatch("1")
                    }
                }
            }
            .withMaxResults(20)
            .build()

        val searchHits =
            elasticsearchOperations.search(query, PerfumeDocument::class.java)

        return searchHits.searchHits.map { SearchPerfumeResponse.from(it) }
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
