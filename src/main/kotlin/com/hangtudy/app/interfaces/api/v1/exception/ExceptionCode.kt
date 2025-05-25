package com.hangtudy.app.interfaces.api.v1.exception

import org.springframework.http.HttpStatus

enum class ExceptionCode(
    val code: String,
    val message: String,
    val httpStatus: HttpStatus
) {
    // 요청 관련 에러
    INVALID_REQUEST_BODY("INVALID_REQUEST_BODY", "요청 본문이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    JSON_PARSE_ERROR("JSON_PARSE_ERROR", "JSON 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_FIELD("MISSING_REQUIRED_FIELD", "필수 필드가 누락되었습니다.", HttpStatus.BAD_REQUEST),
    INVALID_FIELD_TYPE("INVALID_FIELD_TYPE", "필드 타입이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR("VALIDATION_ERROR", "입력값 검증에 실패했습니다.", HttpStatus.BAD_REQUEST),
    
    // 서버 관련 에러
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    NULL_POINTER_ERROR("NULL_POINTER_ERROR", "예상치 못한 null 값이 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    
    // 비즈니스 로직 에러
    INVALID_ARGUMENT("INVALID_ARGUMENT", "잘못된 인수입니다.", HttpStatus.BAD_REQUEST)
}
