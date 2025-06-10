package com.hangtudy.app.infrastructure.repository.user.persistence

import com.hangtudy.app.domain.user.User
import com.hangtudy.app.domain.user.UserRepository
import com.hangtudy.app.infrastructure.repository.user.repository.UserMongoRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryImpl(
    private val userMongoRepository: UserMongoRepository
) : UserRepository {
    
    override fun save(user: User): User {
        return userMongoRepository.save(user)
    }
    
    override fun findByEmail(email: String): User? {
        return userMongoRepository.findByEmail(email)
    }
    
    override fun findById(id: String): User? {
        return userMongoRepository.findById(id).orElse(null)
    }
    
    override fun existsByEmail(email: String): Boolean {
        return userMongoRepository.existsByEmail(email)
    }
}
