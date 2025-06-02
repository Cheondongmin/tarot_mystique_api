package com.hangtudy.app.domain.tarot.message

interface MessageRepository {
    fun save(message: Message)
}