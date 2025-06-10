package com.hangtudy.app.infrastructure.repository.user.repository

import com.hangtudy.app.domain.user.User
import org.springframework.data.mongodb.repository.MongoRepository

interface UserMongoRepository : MongoRepository<User, String> {
    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
}
