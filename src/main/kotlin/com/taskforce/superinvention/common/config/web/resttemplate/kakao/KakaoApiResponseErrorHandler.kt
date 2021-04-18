package com.taskforce.superinvention.common.config.web.resttemplate.kakao

import com.fasterxml.jackson.databind.ObjectMapper
import com.taskforce.superinvention.app.web.dto.kakao.KakaoOAuthResponse
import com.taskforce.superinvention.common.exception.auth.AccessTokenExpiredException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.Series.CLIENT_ERROR
import org.springframework.http.HttpStatus.Series.SERVER_ERROR
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.ResponseErrorHandler

@Component
class KakaoApiResponseErrorHandler (
        val objectMapper: ObjectMapper
): ResponseErrorHandler {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(KakaoApiResponseErrorHandler::class.java)
    }

    override fun hasError(response: ClientHttpResponse): Boolean {
        return (
            response.statusCode.series()    == CLIENT_ERROR // 400 번대
            || response.statusCode.series() == SERVER_ERROR // 500 번대
        )
    }

    override fun handleError(response: ClientHttpResponse) {
        val msg = "[KAKAO-API-ERROR]: 원인 ${response.statusCode} ${response.rawStatusCode} ${response.statusText}"
        LOG.error(msg)

        when (response.statusCode) {
            HttpStatus.UNAUTHORIZED, HttpStatus.BAD_REQUEST -> throw AccessTokenExpiredException("Access Token Expired", HttpStatus.UNAUTHORIZED)
            else -> {
                val authResponse = objectMapper.readValue(response.body, KakaoOAuthResponse::class.java)
                LOG.error("[KAKAO-API-ERROR]: 원인 ${authResponse.code}-${authResponse.msg}")
                throw Exception()
            }
        }
    }
}