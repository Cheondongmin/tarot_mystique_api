package com.hangtudy.app.infrastructure.auth

import com.hangtudy.app.domain.auth.PasswordEncoder
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder as SpringBCrypt
import org.springframework.stereotype.Component

@Component
class BCryptPasswordEncoder : PasswordEncoder {
    private val encoder = SpringBCrypt()
    
    override fun encode(password: String): String {
        return encoder.encode(password)
    }
    
    override fun matches(rawPassword: String, encodedPassword: String): Boolean {
        return encoder.matches(rawPassword, encodedPassword)
    }
}
