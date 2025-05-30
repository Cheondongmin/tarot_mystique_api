package com.hangtudy.app.interfaces.tarot.resolver

import com.hangtudy.app.domain.tarot.TarotService
import com.hangtudy.app.interfaces.common.CommonRes
import com.hangtudy.app.interfaces.common.ResultType
import com.hangtudy.app.interfaces.exception.ExceptionMessage
import com.hangtudy.app.interfaces.tarot.res.GetTarotListRes
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.http.HttpStatus

@Controller
class TarotQueryResolver(
    private val tarotService: TarotService
) {

    @QueryMapping
    fun tarotList(
        @Argument page: Int = 0,
        @Argument limit: Int = 10
    ): CommonRes<Any> {
        return try {
            val pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
            val pagedResult = tarotService.getTarotList(pageable)
            
            val response = GetTarotListRes(
                items = pagedResult.content.map { GetTarotListRes.TarotItemRes.from(it) },
                totalCount = pagedResult.totalElements,
                page = pagedResult.number,
                pageSize = pagedResult.size,
                totalPages = pagedResult.totalPages
            )
            
            CommonRes.success(response)
        } catch (e: Exception) {
            CommonRes(
                resultType = ResultType.FAIL,
                data = GetTarotListRes(
                    items = emptyList(),
                    totalCount = 0,
                    page = 0,
                    pageSize = 0,
                    totalPages = 0
                ),
                exception = ExceptionMessage(e, HttpStatus.INTERNAL_SERVER_ERROR)
            )
        }
    }
}
