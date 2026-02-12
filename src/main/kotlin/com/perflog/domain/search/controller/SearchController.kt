package com.perflog.domain.search.controller

import com.perflog.domain.perfume.model.document.PerfumeDocument
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

    @GetMapping("/perfume")
    fun search(
        @RequestParam keyword: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ResponseEntity<List<PerfumeDocument>> {

        return ResponseEntity.ok(
            searchService.searchPerfume(keyword, page, size)
        )
    }
}