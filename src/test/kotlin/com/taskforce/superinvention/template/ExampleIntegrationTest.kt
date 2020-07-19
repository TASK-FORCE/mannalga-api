package com.taskforce.superinvention.template

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension

@SpringBootTest
class ExampleIntegrationTest {

    @Test
    fun contextLoads() {
        Assertions.assertEquals(1, 1)
    }
}
