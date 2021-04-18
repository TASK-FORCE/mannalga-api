package com.taskforce.superinvention.app.web.common.response


class ResponseDto<T> (
        val data: T,
        val message: String = "success"
) {
    companion object {
        const val EMPTY = ""
    }
}