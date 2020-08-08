package com.taskforce.superinvention.common.config.security

import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

import org.springframework.security.crypto.password.PasswordEncoder

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled=true)
class WebSecurityConfig(
        val jwtTokenProvider: JwtTokenProvider
): WebSecurityConfigurerAdapter() {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder(12)
    }

    @Bean
    override fun authenticationManager(): AuthenticationManager {
        return super.authenticationManager()
    }

    override fun configure(http: HttpSecurity) {

        // Apply JWT
        http.apply(JwtTokenFilterConfigurer(jwtTokenProvider));

        // [1] X-Frame-Options 비활성화
        http.headers().frameOptions().disable()

        // [2] CSRF 방지 비활성화
        http.csrf().disable()

        // [3] Spring Security가 세션을 생성하지 않음
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

        // [4] entry point
        http.authorizeRequests()
            .anyRequest()
            .permitAll()
    }
}