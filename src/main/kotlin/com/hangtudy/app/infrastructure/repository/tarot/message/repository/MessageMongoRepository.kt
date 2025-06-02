package com.hangtudy.app.infrastructure.repository.tarot.message.repository

import com.hangtudy.app.domain.tarot.message.Message
import org.springframework.data.mongodb.repository.MongoRepository

interface MessageMongoRepository: MongoRepository<Message, String> {
    fun save(message: Message): Message
}