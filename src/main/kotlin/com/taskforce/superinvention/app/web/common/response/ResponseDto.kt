package com.taskforce.superinvention.app.web.common.response

import org.apache.http.HttpResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus

class ResponseDto<T> (
        val data: T,
        val message: String = "success"
)