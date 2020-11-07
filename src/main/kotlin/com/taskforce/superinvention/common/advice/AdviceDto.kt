package com.taskforce.superinvention.common.advice


data class ErrorResponse(
        val message: String,
        val stackTrace: List<String> = emptyList()
) {

    constructor(message: String, stackTrace: Array<StackTraceElement>) :
            this(message, stackTrace.sliceArray(0..20).map { it -> it.toString() })
}
