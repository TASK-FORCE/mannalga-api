package com.taskforce.superinvention.common.advice

import com.taskforce.superinvention.common.exception.BizException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest

@RestControllerAdvice
@RestController
class GlobalAdviceController {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(GlobalAdviceController::class.java)
    }

    @ExceptionHandler(BizException::class)
    fun bizExceptionAdvice(e: BizException, webRequest: WebRequest): ResponseEntity<ErrorResponse> {
        return ResponseEntity(ErrorResponse(e.message), e.httpStatus)
    }

    @ExceptionHandler(Exception::class)
    fun globalExceptionAdvice(e: Exception, webRequest: WebRequest): ResponseEntity<ErrorResponse> {
        LOG.error(e.stackTrace.joinToString("\n"))
        return ResponseEntity(ErrorResponse(e.message ?: "", e.stackTrace), HttpStatus.INTERNAL_SERVER_ERROR)
    }
}