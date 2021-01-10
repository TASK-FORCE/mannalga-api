package com.taskforce.superinvention.common.advice


data class ErrorResponse(
        val message: String,
        val stackTrace: String = ""
) {
    companion object {
        const val defaultMessage = "서버에 에러가 발생하였습니다"
    }

    constructor(message: String, stackTrace: Array<StackTraceElement>) :
            this(message, stackTrace.sliceArray(0..20).joinToString (separator = "\n"){ it.toString() })

    constructor(stackTrace: Array<StackTraceElement>):
            this(defaultMessage, stackTrace.sliceArray(0..20).joinToString (separator = "\n"){ it.toString() })
}
