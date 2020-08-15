package com.taskforce.superinvention.app.web.dto.kakao

class KakaoToken (
        val access_token: String? = "",
        val expireds_in  : Int? = 0,
        val refresh_token: String? = "",
        val refresh_token_expires_in: String? = ""
)


class KakaoTokenRefreshRequest (
        val grant_type: String? = "refresh_token",
        val client_id: String,
        val refresh_token: String
)

class KakaoTokenRefreshResponse (
        val expires_in: Long,
        val token_type: String,
        val access_token: String
)

class KakaoUserInfo (
        val id: String,
        val properties: Map<String, Any>,
        val kakao_account: Map<String, Any>
)

class KakaoUserRegistRequest (
        val id: String,
        val kakaoToken: KakaoToken
)