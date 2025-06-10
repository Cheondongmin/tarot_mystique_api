package com.hangtudy.app.application.dto

object AuthDto {
    data class UserDto(
        val id: String,
        val email: String,
        val name: String,
        val role: String
    )

    data class LoginResultDto(
        val token: String,
        val user: UserDto
    )
}
