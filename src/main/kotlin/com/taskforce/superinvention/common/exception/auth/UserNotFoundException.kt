package com.taskforce.superinvention.common.exception.auth

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus

class UserNotFoundException(
        message: String,
        httpStatus: HttpStatus
) : BizException(message, httpStatus) {

        constructor(): this("해당 유저를 찾을 수 없습니다.", HttpStatus.UNAUTHORIZED)
}