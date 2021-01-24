package com.taskforce.superinvention.common.exception.club.meeting

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus

class MeetingMemberOverflowException(message: String) : BizException(message, HttpStatus.CONFLICT) {
}