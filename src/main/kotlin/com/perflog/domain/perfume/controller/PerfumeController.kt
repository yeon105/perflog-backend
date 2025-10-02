package com.perflog.domain.perfume.controller

import com.perflog.domain.perfume.dto.PerfumeDto
import com.perflog.domain.perfume.service.PerfumeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/perfumes")
class PerfumeController(
    private val perfumeService: PerfumeService
) {
    @PostMapping
    fun create(@RequestBody request: PerfumeDto.PerfumeRequest): ResponseEntity<Void> {
        perfumeService.createPerfume(request)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @GetMapping("/{id}")
    fun get(@PathVariable id: Long): ResponseEntity<PerfumeDto.PerfumeResponse> {
        return ResponseEntity.ok(perfumeService.getPerfume(id))
    }

    @GetMapping
    fun list(): ResponseEntity<PerfumeDto.PerfumeListResponse> {
        return ResponseEntity.ok(perfumeService.getPerfumeList())
    }
}