package com.perflog.domain.perfume.service

import com.perflog.common.dto.Paging
import com.perflog.domain.perfume.dto.PerfumeDto
import org.springframework.security.core.Authentication

interface PerfumeService {

    /**
     * 새로운 향수를 등록한다.
     */
    fun createPerfume(request: PerfumeDto.PerfumeRequest, authentication: Authentication)

    /**
     * 특정 향수를 수정한다.
     *
     * @param id 향수 ID
     * @param request 향수 수정 요청 DTO
     * @param authentication 현재 로그인한 사용자 정보 (작성자 검증용)
     * @return 수정된 향수 응답 DTO
     */
    fun updatePerfume(
        id: Long,
        request: PerfumeDto.PerfumeRequest,
        authentication: Authentication
    ): PerfumeDto.PerfumeResponse

    /**
     * 특정 향수를 삭제한다.
     *
     * @param id 향수 ID
     * @param authentication 현재 로그인한 사용자 정보 (작성자 검증용)
     */
    fun deletePerfume(id: Long, authentication: Authentication)

    /**
     * 특정 향수의 상세 정보를 조회한다.
     *
     * @param id 향수 ID
     * @return 향수 상세 응답 DTO
     */
    fun getPerfume(id: Long): PerfumeDto.PerfumeResponse

    /**
     * 모든 향수의 간단한 정보를 조회한다.
     *
     * @return 향수 목록 응답 DTO
     */
    fun getPerfumeList(requestDto: Paging.PageRequestDto): Paging.PageResponseDto<PerfumeDto.PerfumeSimpleResponse>
    fun migrate(): String


}
