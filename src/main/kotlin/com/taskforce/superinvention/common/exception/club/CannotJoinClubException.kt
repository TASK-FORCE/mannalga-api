package com.taskforce.superinvention.common.exception.club

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus

class CannotJoinClubException(message: String,
                              httpStatus: HttpStatus
) : BizException(message, httpStatus) {

    constructor(): this("모임에 가입하실 수 없습니다.", HttpStatus.FORBIDDEN)
}