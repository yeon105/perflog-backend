package com.perflog.domain.member.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import java.time.LocalDateTime

class MemberDto {

    // 로그인 DTO
    data class LoginRequest(
        @field:NotBlank(message = "이메일은 필수 입력 값입니다.")
        @field:Email(message = "이메일 형식이 올바르지 않습니다.")
        val email: String,
        @field:NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        val password: String
    )

    // 회원 요청 DTO
    data class MemberRequest(
        @field:NotBlank(message = "이메일은 필수 입력 값입니다.")
        @field:Email(message = "이메일 형식이 올바르지 않습니다.")
        var email: String,
        @field:NotBlank(message = "비밀번호는 필수 입력 값입니다.")
        var password: String,
        @field:NotBlank(message = "이름은 필수 입력 값입니다.")
        var name: String,
    )

    // 회원 응답 DTO
    data class MemberResponse(
        var email: String,
        var name: String,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime
    )
}