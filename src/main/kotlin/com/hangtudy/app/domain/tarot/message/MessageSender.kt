package com.hangtudy.app.domain.tarot.message

interface MessageSender {
    fun sendMessage(message: String)
}