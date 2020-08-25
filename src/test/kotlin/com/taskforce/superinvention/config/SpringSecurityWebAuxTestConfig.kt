package com.taskforce.superinvention.config

import com.taskforce.superinvention.app.domain.user.User
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager

@TestConfiguration
class SpringSecurityWebAuxTestConfig {

    @Bean
    @Primary
    fun userDetailService(): UserDetailsService{
        val user: User = User("1451001649")
        return InMemoryUserDetailsManager(user)
    }
}