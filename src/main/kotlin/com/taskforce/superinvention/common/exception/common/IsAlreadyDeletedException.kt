package com.taskforce.superinvention.common.exception.common

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus

class IsAlreadyDeletedException(
        message: String,
        httpStatus: HttpStatus
) : BizException(message, httpStatus) {

    constructor(): this("해당 자원은 이미 삭제되었습니다.", HttpStatus.BAD_REQUEST)
}