package com.taskforce.superinvention.common.util.extendFun

import com.taskforce.superinvention.common.exception.BizException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

const val DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
const val DATE_FORMAT = "yyyy-MM-dd"
const val DATE_FORMAT_KOR = "yyyy년 MM월 dd일"
const val TIME_FORMAT = "HH:mm"
const val YEAR_MONTH_KOR = "yyyy년 MM월"

fun LocalDate.toBaseDate(): String {

    return this.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
}

fun LocalDateTime.toBaseDateTime(): String {
    return this.format(DateTimeFormatter.ofPattern(DATE_TIME_FORMAT))
}

fun LocalDate.toKorDate(): String {
    return this.format(DateTimeFormatter.ofPattern(DATE_FORMAT_KOR))
}

fun LocalTime.toBaseTime(): String {
    return this.format(DateTimeFormatter.ofPattern(TIME_FORMAT))
}

fun DayOfWeek.getKorDisplayName(): String {
    return when(this.value) {
        1 -> "월요일"
        2 -> "화요일"
        3 -> "수요일"
        4 -> "목요일"
        5 -> "금요일"
        6 -> "토요일"
        7 -> "일요일"
        else -> throw BizException("존재하지 않는 요일데이터입니다")
    }
}