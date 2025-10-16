package com.perflog.domain.preference.dto

import com.perflog.domain.preference.model.PreferenceStatus
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

class PreferenceDto {

    // 향수 선호 기록 생성 요청 DTO
    data class CreateRequest(
        @field:NotNull(message = "선호 상태는 필수 값입니다.")
        val status: PreferenceStatus, // 선호 여부

        @field:NotNull(message = "사용일은 필수 값입니다.")
        val usedAt: LocalDate? = null // 사용일
    )
}