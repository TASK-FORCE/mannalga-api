package com.taskforce.superinvention.common.advice

import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalControllerExceptionHandler {

    @ExceptionHandler(AccessDeniedException::class)
    fun forbiddenWithNoAuthException() : ErrorResponse{
        return ErrorResponse("권한이 없습니다.")
    }
}

class ErrorResponse(
        val message: String
)