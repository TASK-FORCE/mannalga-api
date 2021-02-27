package com.taskforce.superinvention.app.domain.common.image.webp.convert

data class WebpCompressionParam(
    val q: Int = -1 ,              // RGB 채널 압축 여부 ( 0 ~ 6 )
    val m: Int = -1,               // 압축 방식 지정     ( 0 ~ 6 ) - 높을 수록 고효율 압축, 시간 증가
    val lossless: Boolean = false, // 손실 압축 여부
)
