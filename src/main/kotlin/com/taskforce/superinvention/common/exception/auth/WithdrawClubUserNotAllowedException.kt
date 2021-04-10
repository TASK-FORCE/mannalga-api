package com.taskforce.superinvention.common.exception.auth

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus

class WithdrawClubUserNotAllowedException(
        message: String,
): InsufficientAuthException(message) {
        constructor(): this(
                message = "탈퇴한 모임에서는 사용할 수 없는 기능입니다.",
        )
}
