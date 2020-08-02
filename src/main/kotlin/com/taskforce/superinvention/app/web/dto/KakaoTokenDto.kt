package com.taskforce.superinvention.app.web.dto

class KakaoTokenDto (
    val accessToken: String,
    val expiredsIn  : Int? = 0,
    val refreshToken: String? = "",
    val refreshTokenExpiresIn: String? = ""
){
}