package com.taskforce.superinvention.common.util.extendFun

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun LocalDate.toBaseDate(): String {
    return this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
}

fun LocalDateTime.toBaseDateTime(): String {
    return this.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
}