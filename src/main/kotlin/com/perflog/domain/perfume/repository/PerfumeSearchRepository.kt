package com.perflog.domain.perfume.repository

import co.elastic.clients.elasticsearch.ElasticsearchClient
import co.elastic.clients.elasticsearch._types.SortOrder
import com.perflog.domain.perfume.model.document.PerfumeDocument
import org.springframework.stereotype.Repository

@Repository
class PerfumeSearchRepository(
    private val client: ElasticsearchClient
) {

    private val indexName = "perfumes"

    fun search(keyword: String, page: Int, size: Int): List<PerfumeDocument> {

        val response = client.search(
            { s ->
                s.index(indexName)
                    .from(page * size)
                    .size(size)
                    .query { q ->
                        q.bool { b ->
                            b.must { m ->
                                m.multiMatch { mm ->
                                    mm.query(keyword)
                                        .fields("name^2", "brand")
                                }
                            }
                        }
                    }
                    .sort { sort ->
                        sort.field { f ->
                            f.field("averageRating")
                                .order(SortOrder.Desc)
                        }
                    }
            },
            PerfumeDocument::class.java
        )

        return response.hits().hits()
            .mapNotNull { it.source() }
    }
}
