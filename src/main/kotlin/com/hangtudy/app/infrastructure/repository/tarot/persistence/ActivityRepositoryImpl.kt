package com.hangtudy.app.infrastructure.repository.tarot.persistence

import com.hangtudy.app.domain.tarot.Activity
import com.hangtudy.app.domain.tarot.ActivityRepository
import com.hangtudy.app.infrastructure.repository.tarot.repository.ActivityMongoRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class ActivityRepositoryImpl(
    private val activityMongoRepository: ActivityMongoRepository
) : ActivityRepository {
    override fun save(activity: Activity) {
        activityMongoRepository.save(activity)
    }

    override fun countRecentActivities(minutes: Int): Long {
        val cutoffTime = LocalDateTime.now().minusMinutes(minutes.toLong())
        return activityMongoRepository.countByCreatedAtAfter(cutoffTime)
    }
}