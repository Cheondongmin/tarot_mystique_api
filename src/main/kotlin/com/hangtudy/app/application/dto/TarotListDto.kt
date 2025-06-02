package com.hangtudy.app.application.dto

import com.hangtudy.app.domain.tarot.activity.Activity

data class TarotListDto(
    val items: List<TarotItemDto>,
    val totalCount: Long,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
)

data class TarotItemDto(
    val id: String?,
    val category: String,
    val ipAddress: String,
    val userContent: String,
    val cards: List<Activity.TarotCard>?,
    val createdAtKst: String
) {
    companion object {
        fun from(activity: Activity): TarotItemDto {
            return TarotItemDto(
                id = activity.id,
                category = activity.category,
                ipAddress = activity.ipAddress,
                userContent = activity.userContent,
                cards = activity.resultData?.cards,
                createdAtKst = activity.createdAtKst.toString()
            )
        }
    }
}
