package com.hangtudy.app.domain.auth

interface PasswordEncoder {
    fun encode(password: String): String
    fun matches(rawPassword: String, encodedPassword: String): Boolean
}
