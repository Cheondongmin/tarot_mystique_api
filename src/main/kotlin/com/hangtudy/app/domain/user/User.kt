package com.hangtudy.app.domain.user

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import org.springframework.data.mongodb.core.index.Indexed
import java.time.LocalDateTime

@Document(collection = "Users")
data class User(
    @Id
    val id: String? = null,

    @Field("email")
    @Indexed(unique = true)
    val email: String,

    @Field("password")
    val password: String,

    @Field("name")
    val name: String,

    @Field("role")
    val role: UserRole = UserRole.USER,

    @Field("is_email_verified")
    val isEmailVerified: Boolean = false,

    @Field("email_verification_token")
    val emailVerificationToken: String? = null,

    @Field("email_verification_expires_at")
    val emailVerificationExpiresAt: LocalDateTime? = null,

    @Field("created_at")
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Field("updated_at")
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun create(
            email: String,
            password: String,
            name: String,
            emailVerificationToken: String
        ): User {
            val now = LocalDateTime.now()
            return User(
                email = email.trim(),
                password = password,
                name = name.trim(),
                role = UserRole.USER,
                isEmailVerified = false,
                emailVerificationToken = emailVerificationToken,
                emailVerificationExpiresAt = now.plusHours(24),
                createdAt = now,
                updatedAt = now
            )
        }
    }

    fun markEmailAsVerified(): User {
        return this.copy(
            isEmailVerified = true,
            emailVerificationToken = null,
            emailVerificationExpiresAt = null,
            updatedAt = LocalDateTime.now()
        )
    }
}

enum class UserRole {
    USER,
    ADMIN
}
