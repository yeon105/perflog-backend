package com.perflog.domain.search.service

import com.perflog.domain.perfume.model.document.PerfumeDocument
import com.perflog.domain.perfume.repository.PerfumeSearchRepository
import org.springframework.stereotype.Service

@Service
class SearchServiceImpl(
    private val perfumeSearchRepository: PerfumeSearchRepository
) : SearchService {


    override fun searchPerfume(
        keyword: String,
        page: Int,
        size: Int
    ): List<PerfumeDocument> {
        return perfumeSearchRepository.search(keyword, page, size)
            .map {
                PerfumeDocument(
                    id = it.id,
                    name = it.name,
                    brand = it.brand,
                    season = it.season,
                    gender = it.gender,
                    imageUrl = it.imageUrl,
                )
            }
    }

}