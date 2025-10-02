package com.perflog.domain.preference.service

import com.perflog.domain.preference.dto.PreferenceDto
import org.springframework.security.core.Authentication

interface PerfumePreferenceService {

    /**
     * 새로운 향수 선호 기록을 등록한다.
     *
     * @param id 향수 ID
     * @param request 향수 선호 생성 요청 DTO
     * @param authentication 현재 로그인한 사용자 정보 (작성자 검증용)
     */
    fun recordPerfumePreference(
        id: Long,
        request: PreferenceDto.CreateRequest,
        authentication: Authentication
    )
}