package com.taskforce.superinvention.common.exception.club

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus

class ClubNotFoundException(
        message: String,
        httpStatus: HttpStatus
) : BizException(message, httpStatus) {

    constructor(): this("해당 모임은 존재하지 않습니다.", HttpStatus.BAD_REQUEST)
}