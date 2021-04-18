package com.taskforce.superinvention.app.web.dto.kakao

import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto
import java.time.LocalDate

data class KakaoToken (
        val token_type   : String? = "",
        val access_token : String? = "",
        val expires_in   : Int?    = 0,
        val refresh_token: String? = "",
        val refresh_token_expires_in: Int? = 0
)

data class KakaoOAuthResponse (
    val msg: String,
    val code: Int
)

data class KakaoUserInfo (
        val id: String,
        val properties: KakaoUserProperties,
        val kakao_account: KakaoUserAccount
)

data class KakaoUserRegistRequest (
        val userName: String?,
        val birthday: LocalDate?,
        val profileImageLink: String?,
        val userRegions: List<RegionRequestDto>,
        val userInterests: List<InterestRequestDto>
)

data class KakaoUserProperties (
        val nickname: String,
        val profile_image: String = "",
        val thumbnail_image: String = ""
)

data class KakaoUserAccount (
        val profile_needs_agreement: Boolean,
        val profile: KakaoUserProfile,
        val hasGender: Boolean,
        val gender_needs_agreement: Boolean
)

data class KakaoUserProfile(
        val nickname: String,
        val profile_image_url: String = "",
        val thumbnail_image_url: String = ""
)

