package com.hangtudy.app.infrastructure.repository.tarot.imagehistory.repository

import com.hangtudy.app.domain.tarot.imagehistory.ImageHistory
import org.springframework.data.mongodb.repository.MongoRepository

interface ImageHistoryMongoRepository : MongoRepository<ImageHistory, String> {
}