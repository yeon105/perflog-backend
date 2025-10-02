package com.perflog.domain.member.service

import com.perflog.domain.member.dto.MemberDto
import org.springframework.security.core.Authentication

interface MemberService {

    /**
     * 새로운 회원을 등록한다.
     */
    fun createMember(request: MemberDto.MemberRequest)

    /**
     * 특정 회원의 상세 정보를 조회한다.
     *
     * @return 향수 상세 응답 DTO
     */
    fun getMember(authentication: Authentication): MemberDto.MemberResponse

    /**
     * 특정 회원의 정보를 수정한다.
     *
     * @param request 회원 수정 요청 DTO
     * @param authentication 인증 정보 (로그인 사용자)
     * @return 수정된 회원 응답 DTO
     */
    fun updateMember(
        request: MemberDto.MemberRequest,
        authentication: Authentication
    ): MemberDto.MemberResponse
}
