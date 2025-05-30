package com.hangtudy.app.interfaces.api.v1.tarot.controller

import com.hangtudy.app.interfaces.api.v1.common.CommonRes
import com.hangtudy.app.interfaces.api.v1.tarot.req.AddTarotReq
import com.hangtudy.app.interfaces.api.v1.tarot.req.SendMessageReq
import com.hangtudy.app.interfaces.api.v1.tarot.res.GetTarotListRes
import com.hangtudy.app.domain.tarot.TarotService
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/tarot")
class TarotController(
    private val tarotService: TarotService
) : TarotControllerInterface {
    
    override fun addTarot(
        @Valid @RequestBody req: AddTarotReq,
        httpRequest: HttpServletRequest
    ): CommonRes<String> {
        tarotService.addTarot(req.category, req.userIp, req.userContent, req.resultContent)
        return CommonRes.success("타로 데이터가 성공적으로 저장되었습니다.")
    }

    override fun sendMessage(
        @Valid @RequestBody req: SendMessageReq,
    ): CommonRes<String> {
        tarotService.sendMessage(req.msg, req.userIp)
        return CommonRes.success("관리자에게 메시지가 성공적으로 전송 됐습니다.")
    }
    
    override fun getTarotList(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") limit: Int
    ): CommonRes<GetTarotListRes> {
        val pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"))
        val pagedResult = tarotService.getTarotList(pageable)
        
        val response = GetTarotListRes(
            items = pagedResult.content.map { GetTarotListRes.TarotItemRes.from(it) },
            totalCount = pagedResult.totalElements,
            page = pagedResult.number,
            pageSize = pagedResult.size,
            totalPages = pagedResult.totalPages
        )
        
        return CommonRes.success(response)
    }
}
