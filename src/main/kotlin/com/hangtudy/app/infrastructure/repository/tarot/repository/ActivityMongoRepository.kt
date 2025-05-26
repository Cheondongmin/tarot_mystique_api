package com.hangtudy.app.infrastructure.repository.tarot.repository

import com.hangtudy.app.domain.Tarot.Activity
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDateTime

interface ActivityMongoRepository : MongoRepository<Activity, String> {
    fun save(activity: Activity): Activity
    fun countByCreatedAtAfter(cutoffTime: LocalDateTime): Long
}