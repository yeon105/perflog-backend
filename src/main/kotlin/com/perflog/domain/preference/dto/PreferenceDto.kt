package com.perflog.domain.preference.dto

import com.perflog.domain.preference.model.PreferenceStatus
import java.time.LocalDate

class PreferenceDto {

    // 향수 선호 기록 생성 요청 DTO
    data class CreateRequest(
        val status: PreferenceStatus, // 선호 여부

        val usedAt: LocalDate? = null // 사용일
    )
}