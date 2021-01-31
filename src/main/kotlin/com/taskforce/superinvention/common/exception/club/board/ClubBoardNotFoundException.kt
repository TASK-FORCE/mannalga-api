package com.taskforce.superinvention.common.exception.club.board

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus

class ClubBoardNotFoundException(
        message: String,
        httpStatus: HttpStatus
) : BizException(message, httpStatus) {

    constructor(): this("해당 게시글은 존재하지 않습니다..", HttpStatus.BAD_REQUEST)
}