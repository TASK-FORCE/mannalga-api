package com.taskforce.superinvention.app.web.dto

import org.springframework.util.MultiValueMap

class KakaoUserDto (
        val id: String,
        val properties: Map<String, Any>,
        val kakao_account: Map<String, Any>
) {

}