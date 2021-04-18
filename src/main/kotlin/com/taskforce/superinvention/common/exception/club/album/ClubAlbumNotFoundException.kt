package com.taskforce.superinvention.common.exception.club.album

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus

class ClubAlbumNotFoundException(
        message: String,
        httpStatus: HttpStatus
) : BizException(message, httpStatus) {

    constructor(): this("해당 사진은 모임 사진첩에 존재하지 않습니다..", HttpStatus.BAD_REQUEST)
}