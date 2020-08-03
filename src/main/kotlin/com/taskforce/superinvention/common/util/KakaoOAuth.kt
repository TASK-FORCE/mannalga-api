package com.taskforce.superinvention.common.util

import com.taskforce.superinvention.app.domain.user.UserService
import com.taskforce.superinvention.app.web.dto.KakaoTokenDto
import com.taskforce.superinvention.app.web.dto.KakaoUserDto
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import org.springframework.web.client.RestTemplate

@Component
class KakaoOAuth(
        private var restTemplate: RestTemplate
) {

    fun getKakaoId(token: KakaoTokenDto): String {
        val headers = HttpHeaders()
        headers.set("Authorization", "Bearer ${token.access_token}")

        val request = HttpEntity<MultiValueMap<String, String>>(headers)
        val userProfileRequest = restTemplate.exchange(
                UserService.KAKAO_USER_URI,
                HttpMethod.GET,
                request,
                KakaoUserDto::class.java
        )

        return when (userProfileRequest.statusCode) {
            HttpStatus.OK -> userProfileRequest.body!!.id
            else -> ""
        }
    }
}