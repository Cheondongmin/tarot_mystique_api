package com.hangtudy.app.infrastructure.repository.tarot.repository

import com.hangtudy.app.domain.Tarot.Activity
import org.springframework.data.mongodb.repository.MongoRepository

interface ActivityMongoRepository : MongoRepository<Activity, String> {
    fun save(activity: Activity): Activity
}