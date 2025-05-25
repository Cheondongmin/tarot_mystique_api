package com.hangtudy.app.infrastructure.repository.tarot.persistence

import com.hangtudy.app.domain.Tarot.Activity
import com.hangtudy.app.domain.Tarot.ActivityRepository
import com.hangtudy.app.infrastructure.repository.tarot.repository.ActivityMongoRepository
import org.springframework.stereotype.Repository

@Repository
class ActivityRepositoryImpl(
    private val activityMongoRepository: ActivityMongoRepository
) : ActivityRepository {
    override fun save(activity: Activity) {
        activityMongoRepository.save(activity)
    }
}