package com.perflog.domain.search.service

import com.perflog.domain.perfume.model.document.PerfumeDocument
import com.perflog.domain.perfume.model.enum.SearchTarget
import com.perflog.domain.perfume.repository.PerfumeSearchRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class SearchServiceImpl(
    private val perfumeSearchRepository: PerfumeSearchRepository
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
}