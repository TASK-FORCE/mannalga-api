package com.taskforce.superinvention.app.domain.test

import org.springframework.cache.annotation.Cacheable
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController(
) {
    @GetMapping("/test")
    fun getAllStateList(): String {
        return "CI/CD TEST SUCCESS"
    }
}