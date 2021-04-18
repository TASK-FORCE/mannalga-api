package com.taskforce.superinvention

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.servlet.config.annotation.EnableWebMvc

@SpringBootApplication
class SuperinventionApplication

fun main(args: Array<String>) {
    runApplication<SuperinventionApplication>(*args)
}