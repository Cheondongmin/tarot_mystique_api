package com.hangtudy.app.domain.tarot.activity

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import java.util.*

interface ActivityRepository {
    fun save(activity: Activity)
    fun countRecentActivities(minutes: Int): Long
    fun findAll(pageable: Pageable): Page<Activity>
    fun findById(id: String): Activity?
}
