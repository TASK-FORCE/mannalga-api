package com.taskforce.superinvention.common.config

import com.taskforce.superinvention.common.config.argument.auth.AuthorizeArgumentResolver
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport

@Configuration
class WebMvcConfig(
        private val authorizeArgumentResolver: AuthorizeArgumentResolver
): WebMvcConfigurationSupport() {

    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        argumentResolvers.add(authorizeArgumentResolver)
    }

    @Bean
    fun restTemplate(): RestTemplate {
        val factory = HttpComponentsClientHttpRequestFactory()
        val httpClient: HttpClient = HttpClientBuilder.create()
                .setMaxConnTotal(50)
                .setMaxConnPerRoute(30)
                .build()

        factory.httpClient = httpClient
        factory.setConnectTimeout(2000)
        factory.setReadTimeout(3000)

        return RestTemplate(factory)
    }
}