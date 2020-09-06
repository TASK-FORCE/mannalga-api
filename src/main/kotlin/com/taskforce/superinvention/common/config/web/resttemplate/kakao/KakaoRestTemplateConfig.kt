package com.taskforce.superinvention.common.config.web.resttemplate.kakao

import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.DefaultUriBuilderFactory

@Configuration
class KakaoRestTemplateConfig(
        val kakaoApiResponseErrorHandler: KakaoApiResponseErrorHandler,
        val kakaoAuthResponseErrorHandler: KakaoAuthResponseErrorHandler
){

    companion object {
        const val KAUTH_BASE_URI = "https://kauth.kakao.com"
        const val KAPI_BASE_URI  = "https://kapi.kakao.com"
    }

    @Bean
    fun kakaoApi(): RestTemplate {
        val factory = HttpComponentsClientHttpRequestFactory()
        val httpClient: HttpClient = HttpClientBuilder.create()
                .setMaxConnTotal(40)
                .setMaxConnPerRoute(30)
                .build()

        factory.httpClient = httpClient
        factory.setConnectTimeout(2000)
        factory.setReadTimeout(2000)

        val restTemplate = RestTemplate(factory)
        restTemplate.errorHandler = kakaoApiResponseErrorHandler
        restTemplate.uriTemplateHandler = DefaultUriBuilderFactory(KAPI_BASE_URI)
        return restTemplate
    }

    @Bean
    fun kakaoAuth(): RestTemplate {
        val factory = HttpComponentsClientHttpRequestFactory()
        val httpClient: HttpClient = HttpClientBuilder.create()
                .setMaxConnTotal(30)
                .setMaxConnPerRoute(30)
                .build()

        factory.httpClient = httpClient
        factory.setConnectTimeout(2000)
        factory.setReadTimeout(2000)

        val restTemplate = RestTemplate(factory)
        restTemplate.errorHandler = kakaoAuthResponseErrorHandler
        restTemplate.uriTemplateHandler = DefaultUriBuilderFactory(KAUTH_BASE_URI)

        return restTemplate
    }
}