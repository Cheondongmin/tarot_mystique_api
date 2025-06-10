package com.hangtudy.app.interfaces.auth.controller

import com.hangtudy.app.application.AuthFacade
import com.hangtudy.app.interfaces.auth.req.LoginReq
import com.hangtudy.app.interfaces.auth.req.RegisterReq
import com.hangtudy.app.interfaces.auth.res.LoginRes
import com.hangtudy.app.interfaces.auth.res.UserRes
import com.hangtudy.app.interfaces.common.CommonRes
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authFacade: AuthFacade
) : AuthControllerInterface {

    override fun register(
        @Valid @RequestBody req: RegisterReq
    ): CommonRes<UserRes> {
        val userDto = authFacade.register(req.email, req.password, req.name)
        val response = UserRes.from(userDto)
        return CommonRes.success(response)
    }

    override fun login(
        @Valid @RequestBody req: LoginReq
    ): CommonRes<LoginRes> {
        val loginResult = authFacade.login(req.email, req.password)
        val response = LoginRes.from(loginResult)
        return CommonRes.success(response)
    }

    override fun verifyEmail(
        @RequestParam token: String,
        @RequestParam email: String
    ): CommonRes<UserRes> {
        val verifyResult = authFacade.verifyEmail(token, email)
        val response = UserRes.from(verifyResult)
        return CommonRes.success(response)
    }
}
