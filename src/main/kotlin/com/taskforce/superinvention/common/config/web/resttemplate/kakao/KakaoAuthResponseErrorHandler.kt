package com.taskforce.superinvention.common.config.web.resttemplate.kakao

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.Series.*
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.ResponseErrorHandler

@Component
class KakaoAuthResponseErrorHandler: ResponseErrorHandler {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(KakaoAuthResponseErrorHandler::class.java)
    }

    override fun hasError(response: ClientHttpResponse): Boolean {
        return (
            response.statusCode.series()    == CLIENT_ERROR // 400 번대
            || response.statusCode.series() == SERVER_ERROR // 500 번대
        )
    }

    override fun handleError(response: ClientHttpResponse) {
        LOG.error(response.toString())

        when(response.statusCode.series()) {
            // SERVER_ERROR -> {}
            // CLIENT_ERROR -> {}
            else -> throw Exception()
        }
    }
}