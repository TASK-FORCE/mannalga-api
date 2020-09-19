package com.taskforce.superinvention.common.exception

import org.springframework.http.HttpStatus

open class BizException(
        override val message: String,
        val httpStatus: HttpStatus
) : RuntimeException() {

    companion object {
        private const val serialVersionUID = 1L
    }
    
    constructor(message: String) : this(message, HttpStatus.INTERNAL_SERVER_ERROR)
}