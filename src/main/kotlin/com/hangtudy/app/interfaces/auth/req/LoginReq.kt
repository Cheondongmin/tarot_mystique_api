package com.hangtudy.app.interfaces.auth.req

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

@Schema(description = "로그인 요청")
data class LoginReq(
    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "유효한 이메일 형식이 아닙니다.")
    @Schema(description = "이메일", example = "user@example.com")
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    @Schema(description = "비밀번호", example = "password123")
    val password: String
)
