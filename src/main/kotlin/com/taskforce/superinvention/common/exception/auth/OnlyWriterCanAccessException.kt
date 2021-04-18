package com.taskforce.superinvention.common.exception.auth

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus

class OnlyWriterCanAccessException(
        message: String,
        httpStatus: HttpStatus
) : BizException(message, httpStatus) {
    constructor(message: String): this(message, HttpStatus.FORBIDDEN)
    constructor(): this("오직 해당 자원의 작성자만 자원에 접근할 수 있습니다.", HttpStatus.FORBIDDEN)
}