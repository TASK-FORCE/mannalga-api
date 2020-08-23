package com.taskforce.superinvention.common.utils

import java.util.*

class TokenUtils {
    fun generateToken(seq: Long): String {
        return UUID.fromString(seq.toString()).toString()
    }
}