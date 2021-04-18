package com.taskforce.superinvention.common.advice

import com.taskforce.superinvention.common.util.kakao.KakaoOAuth
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalControllerExceptionHandler {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(KakaoOAuth::class.java)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun forbiddenWithNoAuthException(e: AccessDeniedException) : ResponseEntity<ErrorResponse> {
        LOG.error(e.message)
        return ResponseEntity(
                ErrorResponse("접근 권한이 없습니다.", e.stackTrace),
                HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}