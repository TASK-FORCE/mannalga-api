package com.taskforce.superinvention.common.util.extendFun

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"

fun LocalDate.toBaseDate(): String {
    return this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

fun LocalDateTime.toBaseDateTime(): String {
    return this.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))
}