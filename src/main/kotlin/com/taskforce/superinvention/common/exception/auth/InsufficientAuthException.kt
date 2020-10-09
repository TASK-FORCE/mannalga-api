package com.taskforce.superinvention.common.exception.auth

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus

class InsufficientAuthException(
        message: String,
        httpStatus: HttpStatus
) : BizException(message, httpStatus)