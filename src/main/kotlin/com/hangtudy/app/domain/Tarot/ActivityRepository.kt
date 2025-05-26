package com.hangtudy.app.domain.Tarot

interface ActivityRepository {
    fun save(activity: Activity)
    fun countRecentActivities(minutes: Int): Long
}