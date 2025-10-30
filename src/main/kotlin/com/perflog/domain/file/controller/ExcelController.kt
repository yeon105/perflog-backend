package com.perflog.domain.file.controller

import com.perflog.domain.file.dto.ExcelDto
import com.perflog.domain.file.service.ExcelService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile


@RestController
@RequestMapping("/api/perfumes/excel")
class ExcelController(
    private val excelService: ExcelService
) {
    @PostMapping("/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun upload(
        @RequestParam("file") file: MultipartFile,
        authentication: Authentication
    ): ResponseEntity<ExcelDto.UploadResult> {
        val result = excelService.importPerfumes(file, authentication)
        return ResponseEntity.ok(result)
    }
}