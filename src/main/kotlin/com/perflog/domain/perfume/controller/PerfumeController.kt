package com.perflog.domain.perfume.controller

import com.perflog.domain.perfume.dto.PerfumeDto
import com.perflog.domain.perfume.service.PerfumeService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/perfumes")
class PerfumeController(
    private val perfumeService: PerfumeService
) {
    @PostMapping
    fun createPerfume(@RequestBody request: PerfumeDto.PerfumeRequest): ResponseEntity<Void> {
        perfumeService.createPerfume(request)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PutMapping("/{perfumeId}")
    fun updatePerfume(
        @PathVariable perfumeId: Long,
        @RequestBody request: PerfumeDto.PerfumeRequest, authentication: Authentication
    ): ResponseEntity<PerfumeDto.PerfumeResponse> {
        val response = perfumeService.updatePerfume(perfumeId, request, authentication)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deletePerfume(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<Void> {
        perfumeService.deletePerfume(id, authentication)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/{id}")
    fun getPerfume(@PathVariable id: Long): ResponseEntity<PerfumeDto.PerfumeResponse> {
        return ResponseEntity.ok(perfumeService.getPerfume(id))
    }

    @GetMapping
    fun getPerfumeList(): ResponseEntity<PerfumeDto.PerfumeListResponse> {
        return ResponseEntity.ok(perfumeService.getPerfumeList())
    }

}