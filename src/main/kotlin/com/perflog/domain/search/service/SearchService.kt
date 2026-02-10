package com.perflog.domain.search.service

import com.perflog.common.dto.Paging
import com.perflog.domain.perfume.dto.PerfumeDto
import com.perflog.domain.perfume.model.enum.SearchTarget
import org.springframework.stereotype.Service

@Service
class SearchService {

    /**
     * 이름 또는 브랜드를 기준으로 향수 목록을 검색한다.
     *
     * @return 검색 향수 목록 응답 DTO
     */
    fun searchPerfume(
        target: SearchTarget,
        keyword: String,
        requestDto: Paging.PageRequestDto
    ): Paging.PageResponseDto<PerfumeDto.PerfumeSimpleResponse>
}