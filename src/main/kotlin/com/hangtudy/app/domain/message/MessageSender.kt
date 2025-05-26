package com.hangtudy.app.domain.message

interface MessageSender {
    fun sendMessage(message: String)
}