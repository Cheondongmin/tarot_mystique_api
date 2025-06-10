package com.hangtudy.app.interfaces.auth.req

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "회원가입 요청")
data class RegisterReq(
    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "유효한 이메일 형식이 아닙니다.")
    @Schema(description = "이메일", example = "user@example.com")
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다.")
    @field:Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    @Schema(description = "비밀번호", example = "password123")
    val password: String,

    @field:NotBlank(message = "이름은 필수입니다.")
    @field:Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다.")
    @Schema(description = "사용자 이름", example = "홍길동")
    val name: String
)