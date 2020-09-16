package com.taskforce.superinvention.app.web.common.response

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus
import java.lang.RuntimeException

class ErrorResponseDto(
        val httpStatus: HttpStatus,
        override val message: String
) : RuntimeException()