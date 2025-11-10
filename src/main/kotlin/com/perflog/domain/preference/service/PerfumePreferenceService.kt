package com.perflog.domain.preference.service

import com.perflog.domain.perfume.dto.PerfumeDto
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

    /**
     * 특정 향수의 선호여부를 확인한다.
     */
    fun isPerfumeLiked(perfumeId: Long, authentication: Authentication): Boolean

    /**
     * 선호하는 향수의 간단한 정보를 조회한다.
     *
     * @param authentication 현재 로그인한 사용자 정보
     * @return 선호 향수 목록 응답 DTO
     */
    fun getPreferredPerfumes(authentication: Authentication): List<PerfumeDto.PerfumeSimpleResponse>
}