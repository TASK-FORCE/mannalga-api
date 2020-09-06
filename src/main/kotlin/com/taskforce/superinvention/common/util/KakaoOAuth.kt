package com.taskforce.superinvention.common.util

import com.taskforce.superinvention.app.web.dto.kakao.*
import com.taskforce.superinvention.common.exception.auth.AccessTokenExpiredException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Component
class KakaoOAuth(
        private val kakaoApi: RestTemplate,
        private val kakaoAuth: RestTemplate,

        @Value("\${oauth.kakao.client-id}")
        var client_id: String
) {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(KakaoOAuth::class.java)
        const val KAPI_USER_PROFILE = "/v2/user/me"
        const val KAPI_TOKEN_INFO   = "/v1/user/access_token_info"
        const val KAUTH_TOKEN       = "/oauth/token"

    }

    fun refreshIfTokenExpired(kakaoToken: KakaoToken): KakaoToken {
        val headers = HttpHeaders()
        headers.set("Authorization", "Bearer ${kakaoToken.access_token}")

        val request = HttpEntity<MultiValueMap<String, String>>(headers)
        var token = kakaoToken

        try {
            kakaoApi.exchange( KAPI_TOKEN_INFO, HttpMethod.GET, request, Any::class.java)
        } catch (e: AccessTokenExpiredException) {
            token = refreshKakaoToken(kakaoToken)
        }

        return token
    }

    fun getKakaoUserProfile(kakaoToken: KakaoToken): KakaoUserInfo {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        headers.set("Authorization", "Bearer ${kakaoToken.access_token}")

        val request = HttpEntity<MultiValueMap<String, String>>(headers)
        val userProfile = kakaoApi.exchange( KAPI_USER_PROFILE, HttpMethod.GET, request, KakaoUserInfo::class.java)

        return userProfile.body!!
    }

    private fun refreshKakaoToken(token: KakaoToken): KakaoToken {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val body = LinkedMultiValueMap(
            mapOf(
                "client_id"     to listOf(client_id),
                "refresh_token" to listOf(token.refresh_token!!),
                "grant_type"    to listOf("refresh_token")
            )
        )

        val request = HttpEntity<LinkedMultiValueMap<String, String>>(body, headers)
        val response = kakaoAuth.postForEntity(KAUTH_TOKEN, request, KakaoToken::class.java)
        return response.body!!
    }
}