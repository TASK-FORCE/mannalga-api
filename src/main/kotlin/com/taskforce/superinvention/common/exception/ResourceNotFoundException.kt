package com.taskforce.superinvention.common.exception

import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus

class ResourceNotFoundException(
        message: String,
        httpStatus: HttpStatus
) : BizException(message, httpStatus) {
    constructor(message: String): this(message, HttpStatus.NO_CONTENT)
    constructor(): this("해당 자원이 존재하지 않습니다.", HttpStatus.NO_CONTENT)
}