package com.perflog.domain.perfume.controller

import com.perflog.common.dto.Paging
import com.perflog.domain.perfume.dto.PerfumeDto
import com.perflog.domain.perfume.model.enum.Gender
import com.perflog.domain.perfume.model.enum.Longevity
import com.perflog.domain.perfume.model.enum.Season
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
    fun createPerfume(
        @RequestBody request: PerfumeDto.PerfumeRequest,
        authentication: Authentication
    ): ResponseEntity<Void> {
        perfumeService.createPerfume(request, authentication)
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
    fun getPerfumeList(requestDto: Paging.PageRequestDto): ResponseEntity<Paging.PageResponseDto<PerfumeDto.PerfumeSimpleResponse>> {
        return ResponseEntity.ok(perfumeService.getPerfumeList(requestDto))
    }


    @PostMapping("/bulk-perfume")
    fun bulkCreate(authentication: Authentication): String {

        for (i in 1..300) {

            val request = PerfumeDto.PerfumeRequest(
                name = "조말론블랙베리-$i",
                brand = "조말론",
                launchYear = 2001,
                imageUrl = "https://example.com/perfume.jpg",
                longevity = Longevity.MEDIUM,
                season = Season.SUMMER,
                gender = Gender.MALE,
                topNotes = listOf("Bergamot", "Lemon", "Orange"),
                middleNotes = listOf("Jasmine", "Rose"),
                baseNotes = listOf("Musk", "Cedarwood"),
                tagIds = listOf(1L, 2L, 3L)
            )

            perfumeService.createPerfume(request, authentication)
        }

        return "1000건 생성 완료"
    }

}