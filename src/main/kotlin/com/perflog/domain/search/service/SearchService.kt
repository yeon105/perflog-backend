package com.perflog.domain.search.service

import com.perflog.domain.perfume.model.document.PerfumeDocument
import org.springframework.stereotype.Service

@Service
interface SearchService {

    /**
     * 이름 또는 브랜드를 기준으로 향수 목록을 검색한다.
     *
     * @return 검색 향수 목록 응답 DTO
     */
    fun searchPerfume(keyword: String, page: Int, size: Int): List<PerfumeDocument>

}