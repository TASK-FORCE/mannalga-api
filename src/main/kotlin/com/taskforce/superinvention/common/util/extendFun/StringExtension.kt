package com.taskforce.superinvention.common.util.extendFun

fun String.sliceIfExceed(range: IntRange): String {
    return if(this.length > range.last) {
        this.slice(range)
    } else {
        this
    }
}