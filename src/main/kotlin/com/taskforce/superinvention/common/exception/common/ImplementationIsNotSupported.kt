package com.taskforce.superinvention.common.exception.common

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus

class ImplementationIsNotSupported(
        message: String,
        httpStatus: HttpStatus
) : BizException(message, httpStatus) {

    constructor(): this("해당 요청은 사용될 수 없습니다. (구현체 없음)", HttpStatus.BAD_REQUEST)
}