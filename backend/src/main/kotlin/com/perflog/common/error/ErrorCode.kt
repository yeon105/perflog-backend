package com.perflog.common.error

enum class ErrorCode(
    val status: Int,
    val message: String
) {
    // 공통
    ENTITY_NOT_FOUND(404, "요청한 엔티티를 찾을 수 없습니다."),
    INVALID_ARGUMENT(400, "요청 파라미터가 유효하지 않습니다."),
    INTERNAL_SERVER_ERROR(500, "서버 내부 오류가 발생했습니다."),
    INVALID_ENUM(400, "열거형(enum) 값이 유효하지 않습니다."),

    // 인증/인가 (Security, JWT)
    FORBIDDEN(403, "접근 권한이 없습니다."),
    INVALID_TOKEN(401, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "만료된 토큰입니다."),

    // 회원
    DUPLICATE_EMAIL(409, "이미 가입된 이메일입니다."),
    MEMBER_NOT_FOUND(404, "회원을 찾을 수 없습니다."),

    // 리뷰
    REVIEW_ALREADY_EXISTS(409, "이미 해당 향수에 대한 리뷰가 존재합니다."),
    INVALID_RATING(400, "평점은 1~5 사이여야 합니다."),
    REVIEW_NOT_FOUND(404, "리뷰를 찾을 수 없습니다."),

    // 향수
    PERFUME_NOT_FOUND(404, "해당 향수를 찾을 수 없습니다."),
    DUPLICATE_PERFUME(409, "이미 동일한 향수가 존재합니다."),
    TAG_NOT_FOUND(404, "태그를 찾을 수 없습니다."),

    USED_AT_REQUIRED(400, "사용 일자는 필수입니다.")
}