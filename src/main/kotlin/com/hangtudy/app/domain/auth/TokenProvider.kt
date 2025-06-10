package com.hangtudy.app.domain.auth

import com.hangtudy.app.domain.user.User

interface TokenProvider {
    fun generateToken(user: User): String
    fun validateToken(token: String): Boolean
    fun getUserIdFromToken(token: String): String
    fun getRoleFromToken(token: String): String
}
