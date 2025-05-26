package com.hangtudy.app.domain.tarot

interface ActivityRepository {
    fun save(activity: Activity)
    fun countRecentActivities(minutes: Int): Long
}