package com.taskforce.superinvention.common.exception.club.album

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus

class NoAuthForClubAlbumException(
        message: String,
        httpStatus: HttpStatus
) : BizException(message, httpStatus) {

    constructor(): this("""
        |해당 모임 사진첩 사진에 대한 권한이 없습니다.
        |매니저, 마스터 그리고 사진 등록자만 요청할 수 있습니다.
    """.trimMargin(), HttpStatus.UNAUTHORIZED)
}