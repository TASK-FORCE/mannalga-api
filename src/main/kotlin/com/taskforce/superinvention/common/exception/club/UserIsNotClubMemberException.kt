package com.taskforce.superinvention.common.exception.club

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus

class UserIsNotClubMemberException(
        message: String,
        httpStatus: HttpStatus
) : BizException(message, httpStatus) {
    constructor(): this("해당 유저는 모임원이 아닙니다.", HttpStatus.FORBIDDEN)

}