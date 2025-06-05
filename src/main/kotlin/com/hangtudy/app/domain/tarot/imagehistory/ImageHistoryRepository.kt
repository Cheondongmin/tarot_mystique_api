package com.hangtudy.app.domain.tarot.imagehistory

interface ImageHistoryRepository {
    fun save(imageHistory: ImageHistory): ImageHistory
}