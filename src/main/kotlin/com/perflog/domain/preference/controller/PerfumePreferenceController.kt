package com.perflog.domain.preference.controller

import com.perflog.domain.preference.dto.PreferenceDto
import com.perflog.domain.preference.service.PerfumePreferenceService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/preferences")
class PerfumePreferenceController(
    private val preferenceService: PerfumePreferenceService
) {

    @PostMapping("/{perfumeId}")
    fun create(
        @PathVariable perfumeId: Long,
        @Valid @RequestBody request: PreferenceDto.CreateRequest,
        authentication: Authentication
    ): ResponseEntity<Void> {
        preferenceService.recordPerfumePreference(perfumeId, request, authentication)
        return ResponseEntity.ok().build()
    }
}