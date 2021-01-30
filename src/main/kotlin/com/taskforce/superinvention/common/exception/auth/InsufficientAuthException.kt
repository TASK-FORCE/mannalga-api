package com.taskforce.superinvention.common.exception.auth

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus

class InsufficientAuthException(
        message: String,
        httpStatus: HttpStatus
) : BizException(message, httpStatus) {
        constructor(): this(
                message    = "권한이 충분하지 않습니다.",
                httpStatus = HttpStatus.UNAUTHORIZED
        )
}