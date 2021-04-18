package com.taskforce.superinvention.common.exception.club.meeting

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus

class MeetingIsClosedException(message: String) : BizException(message, HttpStatus.CONFLICT) {
    constructor(): this("이미 종료된 만남입니다")
}