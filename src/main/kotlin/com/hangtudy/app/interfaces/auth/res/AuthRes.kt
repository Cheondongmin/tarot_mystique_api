package com.hangtudy.app.interfaces.auth.res

import com.hangtudy.app.application.dto.AuthDto
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "사용자 정보 응답")
data class UserRes(
    @Schema(description = "사용자 ID")
    val id: String,
    
    @Schema(description = "이메일")
    val email: String,
    
    @Schema(description = "사용자 이름")
    val name: String,
    
    @Schema(description = "권한", example = "USER")
    val role: String
) {
    companion object {
        fun from(dto: AuthDto.UserDto): UserRes {
            return UserRes(
                id = dto.id,
                email = dto.email,
                name = dto.name,
                role = dto.role
            )
        }
    }
}

@Schema(description = "로그인 응답")
data class LoginRes(
    @Schema(description = "JWT 토큰")
    val token: String,
    
    @Schema(description = "사용자 정보")
    val user: UserRes
) {
    companion object {
        fun from(dto: AuthDto.LoginResultDto): LoginRes {
            return LoginRes(
                token = dto.token,
                user = UserRes(
                    id = dto.user.id,
                    email = dto.user.email,
                    name = dto.user.name,
                    role = dto.user.role
                )
            )
        }
    }
}
