package com.hangtudy.app.infrastructure.repository.tarot.activity.repository

import com.hangtudy.app.domain.tarot.activity.Activity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDateTime

interface ActivityMongoRepository : MongoRepository<Activity, String> {
    fun countByCreatedAtAfter(cutoffTime: LocalDateTime): Long
    fun findAllByOrderByCreatedAtDesc(pageable: Pageable): Page<Activity>
}
