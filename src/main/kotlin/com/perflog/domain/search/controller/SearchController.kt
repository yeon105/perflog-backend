package com.perflog.domain.search.controller

import com.perflog.domain.perfume.dto.PerfumeDto
import com.perflog.domain.perfume.model.enum.SearchTarget
import com.perflog.domain.search.dto.SearchPerfumeResponse
import com.perflog.domain.search.service.SearchService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * 향수 정보를 검색하는 컨트롤러 클래스입니다.
 */
@RestController
@RequestMapping("/api/search")
class SearchController
    (
    private val searchService: SearchService
) {

    /**
     * 키워드와 검색 대상을 기반으로 향수를 검색합니다.
     * @param keyword 검색 키워드
     * @param target 검색 대상 (이름, 브랜드 등)
     * @return 검색된 향수 목록
     */
    @GetMapping("/perfume")
    fun searchPerfume(
        @RequestParam keyword: String,
        @RequestParam target: SearchTarget
    ): List<SearchPerfumeResponse> {
        return searchService.searchPerfume(keyword, target)
    }

    /**
     * 입력된 키워드에 대한 자동완성 목록을 제공합니다.
     * @param keyword 자동완성 키워드
     * @return 자동완성된 향수 정보 목록
     */
    @GetMapping("/autocomplete")
    fun autocomplete(
        @RequestParam keyword: String
    ): ResponseEntity<List<PerfumeDto.autocomplete>> {

        return ResponseEntity.ok(
            searchService.autocomplete(keyword)
        )
    }
}