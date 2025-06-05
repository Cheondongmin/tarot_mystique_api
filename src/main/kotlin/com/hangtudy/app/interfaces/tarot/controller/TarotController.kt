package com.hangtudy.app.interfaces.tarot.controller

import com.hangtudy.app.application.TarotFacade
import com.hangtudy.app.interfaces.common.CommonRes
import com.hangtudy.app.interfaces.tarot.req.AddImageHistoryReq
import com.hangtudy.app.interfaces.tarot.req.AddTarotReq
import com.hangtudy.app.interfaces.tarot.req.SendMessageReq
import com.hangtudy.app.interfaces.tarot.res.GetTarotListRes
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/tarot")
class TarotController(
    private val tarotFacade: TarotFacade
) : TarotControllerInterface {

    override fun addTarot(
        @Valid @RequestBody req: AddTarotReq,
        httpRequest: HttpServletRequest
    ): CommonRes<String> {
        tarotFacade.addTarot(req.category, req.userIp, req.userContent, req.resultContent)
        return CommonRes.success("타로 데이터가 성공적으로 저장되었습니다.")
    }

    override fun sendMessage(
        @Valid @RequestBody req: SendMessageReq,
    ): CommonRes<String> {
        tarotFacade.sendMessage(req.msg, req.userIp)
        return CommonRes.success("관리자에게 메시지가 성공적으로 전송 됐습니다.")
    }

    override fun getTarotList(
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") limit: Int
    ): CommonRes<GetTarotListRes> {
        val dto = tarotFacade.getTarotList(page, limit)
        val response = GetTarotListRes.from(dto)
        return CommonRes.success(response)
    }

    override fun addImageHistory(
        @Valid @RequestBody req: AddImageHistoryReq
    ): CommonRes<String> {
        tarotFacade.addImageHistory(
            req.activityId,
            req.imageActionType,
            req.userAgent,
            req.browserName,
            req.osName,
            req.deviceType,
            req.ipAddress
        )
        return CommonRes.success("이미지 히스토리 데이터가 성공적으로 저장되었습니다.")
    }
}
