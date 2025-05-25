package com.hangtudy.app.interfaces.api.v1.exception

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.hangtudy.app.interfaces.api.v1.common.CommonRes
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ApiExceptionHandler {
    private val logger = LoggerFactory.getLogger(ApiExceptionHandler::class.java)

    // JSON 파싱 에러 처리
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<CommonRes<*>> {
        val rootCause = e.rootCause
        
        return when (rootCause) {
            is JsonParseException -> {
                logger.warn("JSON parse error: ${rootCause.message}", e)
                ResponseEntity.badRequest().body(
                    CommonRes.error(ExceptionCode.JSON_PARSE_ERROR, rootCause.message)
                )
            }
            is MismatchedInputException -> {
                val fieldName = rootCause.path.joinToString(".") { it.fieldName ?: "[${it.index}]" }
                val errorMessage = "필수 필드 '$fieldName'이(가) 누락되었거나 잘못된 형식입니다."
                logger.warn("Missing or invalid required field: $fieldName", e)
                ResponseEntity.badRequest().body(
                    CommonRes.error(ExceptionCode.MISSING_REQUIRED_FIELD, errorMessage)
                )
            }
            is InvalidFormatException -> {
                val fieldName = rootCause.path.joinToString(".") { it.fieldName ?: "[${it.index}]" }
                val expectedType = rootCause.targetType.simpleName
                val errorMessage = "필드 '$fieldName'의 형식이 올바르지 않습니다. 예상 타입: $expectedType"
                logger.warn("Invalid field format for field: $fieldName, expected: $expectedType", e)
                ResponseEntity.badRequest().body(
                    CommonRes.error(ExceptionCode.INVALID_FIELD_TYPE, errorMessage)
                )
            }
            is JsonMappingException -> {
                val fieldName = rootCause.path.joinToString(".") { it.fieldName ?: "[${it.index}]" }
                val errorMessage = "JSON 매핑 오류: 필드 '$fieldName' 처리 중 문제가 발생했습니다."
                logger.warn("JSON mapping error for field: $fieldName", e)
                ResponseEntity.badRequest().body(
                    CommonRes.error(ExceptionCode.JSON_PARSE_ERROR, errorMessage)
                )
            }
            else -> {
                logger.warn("Request body read error: ${e.message}", e)
                ResponseEntity.badRequest().body(
                    CommonRes.error(ExceptionCode.INVALID_REQUEST_BODY, "요청 본문을 읽을 수 없습니다.")
                )
            }
        }
    }

    // Validation 에러 처리
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<CommonRes<*>> {
        val errorMessages = e.bindingResult.fieldErrors
            .map { "${it.field}: ${it.defaultMessage}" }
        
        val errorMessage = errorMessages.joinToString(", ")
        logger.warn("Validation error: $errorMessage", e)
        
        return ResponseEntity.badRequest().body(
            CommonRes.error(ExceptionCode.VALIDATION_ERROR, errorMessage)
        )
    }

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<CommonRes<*>> {
        logger.warn("IllegalArgumentException: ${e.message}", e)
        
        return ResponseEntity.badRequest().body(
            CommonRes.error(ExceptionCode.INVALID_ARGUMENT, e.message ?: "잘못된 인수입니다.")
        )
    }

    // NullPointerException 처리
    @ExceptionHandler(NullPointerException::class)
    fun handleNullPointerException(e: NullPointerException): ResponseEntity<CommonRes<*>> {
        logger.error("NullPointerException: ${e.message}", e)
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            CommonRes.error(ExceptionCode.NULL_POINTER_ERROR, "예상치 못한 null 값이 발생했습니다.")
        )
    }

    // RuntimeException 처리
    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(e: RuntimeException): ResponseEntity<CommonRes<*>> {
        logger.error("RuntimeException: ${e.message}", e)
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            CommonRes.error(ExceptionCode.INTERNAL_SERVER_ERROR, e.message ?: "서버 내부 오류가 발생했습니다.")
        )
    }

    // 기타 모든 Exception 처리
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<CommonRes<*>> {
        logger.error("Unexpected exception: ${e.message}", e)
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            CommonRes.error(ExceptionCode.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.")
        )
    }
}
