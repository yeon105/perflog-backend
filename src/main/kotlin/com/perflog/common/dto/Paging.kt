package com.perflog.common.dto

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort

class Paging {

    // 페이지 요청 DTO
    data class PageRequestDto(
        val page: Int = 1, // 현재 페이지 번호 (1부터 시작)
        val size: Int = 10, // 한 페이지에 보여줄 데이터 개수
        val sort: String? = null,  // 정렬 기준 (예: "name,asc")
    ) {
        fun toPageable(defaultSort: Sort = Sort.unsorted()): Pageable {
            val pageIndex = (if (page < 1) 1 else page) - 1
            val resolvedSort = sort?.let {
                val (property, direction) = it.split(",")
                val dir = if (direction == "asc") Sort.Direction.ASC else Sort.Direction.DESC
                Sort.by(dir, property)
            } ?: defaultSort
            return PageRequest.of(pageIndex, size, resolvedSort)
        }
    }

    // 페이지 응답 DTO
    data class PageResponseDto<T>(
        val items: List<T>, // 실제 데이터 목록 (요청한 페이지의 DTO 리스트)
        val meta: PageMeta // 페이지네이션 정보 (현재 페이지, 전체 페이지, 다음/이전 여부 등)
    )

    // 페이지네이션 부가 정보
    data class PageMeta(
        val page: Int, // 현재 페이지 번호
        val size: Int,  // 한 페이지당 데이터 개수
        val totalElements: Long, // 전체 데이터 개수 (DB 기준)
        val totalPages: Int, // 전체 페이지 수 (총 데이터 ÷ size)
        val hasNext: Boolean, // 다음 페이지 존재 여부
        val hasPrev: Boolean // 이전 페이지 존재 여부
    )
}