package com.hangtudy.app.application

import com.hangtudy.app.application.dto.AuthDto
import com.hangtudy.app.domain.user.UserService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class AuthFacade(
    private val userService: UserService
) {
    private val logger = LoggerFactory.getLogger(AuthFacade::class.java)

    fun register(email: String, password: String, name: String): AuthDto.UserDto {
        return userService.register(email, password, name)
            .map { user ->
                AuthDto.UserDto(
                    id = user.id!!,
                    email = user.email,
                    name = user.name,
                    role = user.role.name
                )
            }
            .onFailure { e ->
                logger.error("❌ Registration failed: ${e.message}", e)
                throw e
            }
            .getOrThrow()
    }

    fun login(email: String, password: String): AuthDto.LoginResultDto {
        return userService.login(email, password)
            .map { (token, user) ->
                AuthDto.LoginResultDto(
                    token = token,
                    user = AuthDto.UserDto(
                        id = user.id!!,
                        email = user.email,
                        name = user.name,
                        role = user.role.name
                    )
                )
            }
            .onFailure { e ->
                logger.error("❌ Login failed: ${e.message}", e)
                throw e
            }
            .getOrThrow()
    }

    fun verifyEmail(token :String, email: String): AuthDto.UserDto {
        return userService.verifyEmail(token, email)
            .map { user ->
                AuthDto.UserDto(
                    id = user.id!!,
                    email = user.email,
                    name = user.name,
                    role = user.role.name
                )
            }
            .onFailure { e ->
                logger.error("❌ verify failed: ${e.message}", e)
                throw e
            }
            .getOrThrow()
    }
}
