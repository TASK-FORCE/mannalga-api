package com.taskforce.superinvention.app.web.dto

class KakaoTokenDto (
        val access_token: String? = "",
        val expireds_in  : Int? = 0,
        val refresh_token: String? = "",
        val refresh_token_expires_in: String? = ""
){
}