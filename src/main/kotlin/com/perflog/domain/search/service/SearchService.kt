package com.perflog.domain.search.service

import com.perflog.domain.perfume.dto.PerfumeDto
import com.perflog.domain.perfume.model.enum.SearchTarget
import com.perflog.domain.search.dto.SearchPerfumeResponse

interface SearchService {

    /**
     * 이름 또는 브랜드를 기준으로 향수 목록을 검색한다.
     *
     * @return 검색 향수 목록 응답 DTO
     */
    fun searchPerfume(keyword: String, target: SearchTarget): List<SearchPerfumeResponse>

    fun autocomplete(keyword: String): List<PerfumeDto.autocomplete>


}