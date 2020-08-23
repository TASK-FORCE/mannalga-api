package com.taskforce.superinvention.app.web.dto.kakao

import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.state.StateRequestDto
import java.time.LocalDate

class KakaoToken (
        val access_token: String? = "",
        val expireds_in  : Int? = 0,
        val refresh_token: String? = "",
        val refresh_token_expires_in: Int? = 0
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
        val properties: KakaoUserProperties,
        val kakao_account: KakaoUserAccount
)

class KakaoUserRegistRequest (
        val userName: String?,
        val birthday: LocalDate?,
        val profileImageLink: String?,
        val userStates: List<StateRequestDto>,
        val userInterests: List<InterestRequestDto>
)

class KakaoUserProperties (
        val nickname: String,
        val profile_image: String = "",
        val thumbnail_image: String = ""
)

class KakaoUserProfile(
        val nickname: String,
        val profile_image_url: String = "",
        val thumbnail_image_url: String = ""
)

class KakaoUserAccount (
        val profile_needs_agreement: Boolean,
        val profile: KakaoUserProfile,
        val hasGender: Boolean,
        val gender_needs_agreement: Boolean
)