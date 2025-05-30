package com.hangtudy.app.interfaces.tarot.resolver

import com.hangtudy.app.domain.tarot.TarotService
import com.hangtudy.app.interfaces.common.CommonRes
import com.hangtudy.app.interfaces.common.ResultType
import com.hangtudy.app.interfaces.exception.ExceptionMessage
import com.hangtudy.app.interfaces.tarot.req.AddTarotReq
import com.hangtudy.app.interfaces.tarot.req.SendMessageReq
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.stereotype.Controller
import org.springframework.http.HttpStatus

@Controller
class TarotMutationResolver(
    private val tarotService: TarotService
) {

    @MutationMapping
    fun addTarot(@Argument input: AddTarotReq): CommonRes<String> {
        return try {
            tarotService.addTarot(
                category = input.category,
                userIp = input.userIp,
                userContent = input.userContent,
                resultContent = input.resultContent
            )
            
            CommonRes.success("타로 데이터가 성공적으로 저장되었습니다.")
        } catch (e: Exception) {
            CommonRes(
                resultType = ResultType.FAIL,
                data = "타로 데이터 저장 실패",
                exception = ExceptionMessage(e, HttpStatus.INTERNAL_SERVER_ERROR)
            )
        }
    }

    @MutationMapping
    fun sendMessage(@Argument input: SendMessageReq): CommonRes<String> {
        return try {
            tarotService.sendMessage(input.msg, input.userIp)
            
            CommonRes.success("관리자에게 메시지가 성공적으로 전송됐습니다.")
        } catch (e: Exception) {
            CommonRes(
                resultType = ResultType.FAIL,
                data = "메시지 전송 실패",
                exception = ExceptionMessage(e, HttpStatus.INTERNAL_SERVER_ERROR)
            )
        }
    }
}
