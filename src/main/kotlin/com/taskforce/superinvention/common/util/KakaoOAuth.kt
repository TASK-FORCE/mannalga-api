package com.taskforce.superinvention.common.util

import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.UserType
import com.taskforce.superinvention.app.model.AppToken
import com.taskforce.superinvention.app.web.dto.kakao.*
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate
import java.lang.Exception

@Component
class KakaoOAuth(
        private var restTemplate: RestTemplate,
        private var jwtTokenProvider: JwtTokenProvider,

        @Value("\${oauth.kakao.client-id}")
        var client_id: String
) {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(KakaoOAuth::class.java)
        const val USER_INFO_URI = "https://kapi.kakao.com/v2/user/me"
        const val TOKEN_URI     = "https://kauth.kakao.com/oauth/token"
    }

    fun getKakaoUserId(token: KakaoToken): String {
        val headers = HttpHeaders()
        headers.set("Authorization", "Bearer ${token.access_token}")

        val request = HttpEntity<MultiValueMap<String, String>>(headers)
        val userProfileRequest = restTemplate.exchange(
                USER_INFO_URI,
                HttpMethod.GET,
                request,
                KakaoUserInfo::class.java
        )

        return when (userProfileRequest.statusCode) {
            HttpStatus.OK -> userProfileRequest.body!!.id
            else -> ""
        }
    }

    fun getKakaoUserProfile(accessToken: String): KakaoUserInfo {
        val headers = HttpHeaders()
        headers.set("Authorization", "Bearer $accessToken")

        val request = HttpEntity<MultiValueMap<String, String>>(headers)
        val userProfile = restTemplate.exchange(
                USER_INFO_URI,
                HttpMethod.GET,
                request,
                KakaoUserInfo::class.java
        )
        return userProfile.body!!
    }

    fun refreshKakaoToken(user: User): String {
        if(user.userType != UserType.KAKAO) {
            throw Exception()
        }

        val param = KakaoTokenRefreshRequest(client_id = client_id, refresh_token = user.refrestToken!!)
        val response = restTemplate.postForEntity(TOKEN_URI, param , KakaoTokenRefreshResponse::class.java)

        if(response.statusCode != HttpStatus.OK) {
            LOG.error("[Refresh Token Error]")
            throw Exception()
        }
        return response.body!!.access_token
    }

    fun publishAppToken(isFirst: Boolean, user: User): AppToken {
        return AppToken(
                isFirst,
                jwtTokenProvider.createAppToken(user.userId, user.userRoles)
        )
    }
}