package com.taskforce.superinvention.common.exception

import org.springframework.http.HttpStatus

class BizException(
        override val message: String,
        val httpStatus: HttpStatus
) : RuntimeException() {

    companion object {
        private const val serialVersionUID = 1L
    }
}