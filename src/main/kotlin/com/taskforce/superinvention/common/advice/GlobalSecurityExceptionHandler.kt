package com.taskforce.superinvention.common.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalControllerExceptionHandler {

    @ExceptionHandler(AccessDeniedException::class)
    fun forbiddenWithNoAuthException(e: AccessDeniedException) : ResponseEntity<ErrorResponse> {
        return ResponseEntity(
                ErrorResponse("접근 권한이 없습니다.", e.stackTrace),
                HttpStatus.INTERNAL_SERVER_ERROR
        )
    }
}