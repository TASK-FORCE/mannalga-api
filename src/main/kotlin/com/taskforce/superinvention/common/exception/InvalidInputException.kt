package com.taskforce.superinvention.common.exception

import org.springframework.http.HttpStatus

class InvalidInputException(
        message: String,
        httpStatus: HttpStatus
) : BizException(message, httpStatus) {
    constructor(message: String): this(message, HttpStatus.BAD_REQUEST)
    constructor(): this("입력값이 올바르지않습니다.", HttpStatus.BAD_REQUEST)
}