package com.hangtudy.app.domain.email

interface EmailSender {
    fun sendVerificationEmail(email: String, token: String): Result<Unit>
}
