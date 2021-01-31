package com.taskforce.superinvention.common.extendFun

import com.taskforce.superinvention.common.util.extendFun.sliceIfExceed
import com.taskforce.superinvention.config.test.MockkTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class StringExtensionTest: MockkTest() {

    @Test
    fun `sliceIfExceed - 범위를 넘어섯을 때만 문자열을 자름`() {

        val str  = "문자열-10-글자-"
        val str2 = "3글자"

        Assertions.assertEquals("문자열", str.sliceIfExceed(0 until 3))
        Assertions.assertEquals("3글자" , str2.sliceIfExceed(0 until 3))
    }

    @Test
    fun `sliceIfExceed - 범위 내에 있을때는 문자열을 자르지 않음`() {
        val str  = "문자열-10-글자-"
        Assertions.assertEquals(str, str.sliceIfExceed(0 until 100))
    }
}