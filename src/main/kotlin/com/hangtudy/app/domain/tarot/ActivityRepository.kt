package com.hangtudy.app.domain.tarot

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ActivityRepository {
    fun save(activity: Activity)
    fun countRecentActivities(minutes: Int): Long
    fun findAll(pageable: Pageable): Page<Activity>
}
