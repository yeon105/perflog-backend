package com.perflog.domain.search.controller

import com.perflog.common.dto.Paging
import com.perflog.domain.perfume.dto.PerfumeDto
import com.perflog.domain.perfume.model.enum.SearchTarget
import com.perflog.domain.search.service.SearchService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/search")
class SearchController(
    private val searchService: SearchService
) {
    
    @GetMapping()
    fun searchPerfume(
        @RequestParam target: SearchTarget,
        @RequestParam keyword: String,
        requestDto: Paging.PageRequestDto
    ): ResponseEntity<Paging.PageResponseDto<PerfumeDto.PerfumeSimpleResponse>> {
        return ResponseEntity.ok(searchService.searchPerfume(target, keyword, requestDto))
    }
}