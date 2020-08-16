package com.taskforce.superinvention.common.advice

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class GlobalControllerExceptionHandler: ResponseEntityExceptionHandler() {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun pageNotFoundException() : ResponseEntity<Any>{
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun internalServerException() : ResponseEntity<Any>{
        return ResponseEntity(HttpStatus.NOT_FOUND)
    }
}