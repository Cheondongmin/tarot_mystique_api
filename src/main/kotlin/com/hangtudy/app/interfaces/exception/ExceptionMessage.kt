package com.hangtudy.app.interfaces.exception

import org.springframework.http.HttpStatus

data class ExceptionMessage(
    val code: String = "SUCCESS",
    val message: String = "요청이 성공적으로 처리되었습니다.",
    val data: Any? = null
) {
    constructor(error: Exception, status: HttpStatus) : this(
        code = status.name,
        message = error.message ?: "에러가 발생했습니다.",
        data = null
    )

    constructor(error: ExceptionCode, errorData: Any?) : this(
        code = error.code,
        message = error.message,
        data = errorData
    )
}
