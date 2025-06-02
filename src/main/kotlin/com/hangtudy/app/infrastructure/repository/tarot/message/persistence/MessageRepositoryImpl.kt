package com.hangtudy.app.infrastructure.repository.tarot.message.persistence

import com.hangtudy.app.domain.tarot.message.Message
import com.hangtudy.app.domain.tarot.message.MessageRepository
import com.hangtudy.app.infrastructure.repository.tarot.message.repository.MessageMongoRepository
import org.springframework.stereotype.Repository

@Repository
class MessageRepositoryImpl(
    private val messageMongoRepository: MessageMongoRepository
): MessageRepository {
    override fun save(message: Message) {
        messageMongoRepository.save(message)
    }
}