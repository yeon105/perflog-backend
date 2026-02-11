package com.perflog.domain.search.controller

import com.perflog.domain.perfume.model.entity.PerfumeDocument
import com.perflog.domain.perfume.model.enum.SearchTarget
import com.perflog.domain.search.service.SearchService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/search")
class SearchController(
    private val searchService: SearchService
) {

    @GetMapping()
    fun searchPerfume(
        @RequestParam target: SearchTarget,
        @RequestParam keyword: String,
//        requestDto: Paging.PageRequestDto
    ): List<PerfumeDocument> {
        return searchService.searchPerfume(target, keyword)
    }

    @PostMapping("/reindex")
    fun reindex(): String {
        return searchService.reindexAll()
    }
}