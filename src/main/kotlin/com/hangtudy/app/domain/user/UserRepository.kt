package com.hangtudy.app.domain.user

interface UserRepository {
    fun save(user: User): User
    fun findByEmail(email: String): User?
    fun findById(id: String): User?
    fun existsByEmail(email: String): Boolean
}
