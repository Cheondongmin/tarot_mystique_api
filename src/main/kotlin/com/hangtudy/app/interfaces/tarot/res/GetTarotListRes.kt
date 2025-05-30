package com.hangtudy.app.interfaces.tarot.res

import com.hangtudy.app.domain.tarot.Activity
import io.swagger.v3.oas.annotations.media.Schema
import lombok.NoArgsConstructor

@Schema(description = "타로 목록 조회 응답")
@NoArgsConstructor
data class GetTarotListRes(
    @Schema(description = "타로 목록")
    val items: List<TarotItemRes>,
    @Schema(description = "전체 개수")
    val totalCount: Long,
    @Schema(description = "현재 페이지 (0부터 시작)")
    val page: Int,
    @Schema(description = "페이지 크기")
    val pageSize: Int,
    @Schema(description = "전체 페이지 수")
    val totalPages: Int
) {
    @Schema(description = "타로 항목")
    data class TarotItemRes(
        @Schema(description = "ID")
        val id: String?,
        @Schema(description = "카테고리")
        val category: String,
        @Schema(description = "IP 주소")
        val ipAddress: String,
        @Schema(description = "사용자 입력 내용")
        val userContent: String,
        @Schema(description = "카드 목록")
        val cards: List<Activity.TarotCard>?,
        @Schema(description = "생성일시 (KST)")
        val createdAtKst: String
    ) {
        companion object {
            fun from(activity: Activity): TarotItemRes {
                return TarotItemRes(
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
}
