package com.hangtudy.app.interfaces.exception

import com.hangtudy.app.domain.user.DuplicateEmailException
import com.hangtudy.app.domain.user.EmailNotVerifiedException
import com.hangtudy.app.domain.user.InvalidCredentialsException
import com.hangtudy.app.domain.user.UserNotFoundException
import com.hangtudy.app.interfaces.common.CommonRes
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class AuthExceptionHandler {
    private val logger = LoggerFactory.getLogger(AuthExceptionHandler::class.java)
    
    @ExceptionHandler(DuplicateEmailException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleDuplicateEmail(e: DuplicateEmailException): CommonRes<Map<String, Any>> {
        logger.warn("Duplicate email registration attempt: ${e.message}")
        return CommonRes.error(e, HttpStatus.CONFLICT)
    }
    
    @ExceptionHandler(InvalidCredentialsException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleInvalidCredentials(e: InvalidCredentialsException): CommonRes<Map<String, Any>> {
        logger.warn("Invalid login attempt: ${e.message}")
        return CommonRes.error(e, HttpStatus.UNAUTHORIZED)
    }
    
    @ExceptionHandler(EmailNotVerifiedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleEmailNotVerified(e: EmailNotVerifiedException): CommonRes<Map<String, Any>> {
        logger.warn("Login attempt with unverified email: ${e.message}")
        return CommonRes.error(e, HttpStatus.FORBIDDEN)
    }
    
    @ExceptionHandler(UserNotFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleUserNotFound(e: UserNotFoundException): CommonRes<Map<String, Any>> {
        logger.warn("User not found: ${e.message}")
        return CommonRes.error(e, HttpStatus.NOT_FOUND)
    }
}
