package com.perflog.domain.preference.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern

class PreferenceDto {

    // 향수 선호 기록 생성 요청 DTO
    data class CreateRequest(
        @field:NotNull
        @field:Pattern(regexp = "(?i)LIKE|DISLIKE", message = "status는 LIKE 또는 DISLIKE만 가능합니다.")
        val status: String, // 선호 여부

        val usedAt: String? = null // 사용일
    )
}