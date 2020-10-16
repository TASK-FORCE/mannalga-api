package com.taskforce.superinvention.app.web.dto.user.info

import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.common.util.extendFun.toBaseDate

data class UserInfoDto(
        val userName: String = "",
        val birthday: String = "",
        val profileImageLink: String = "",
        val userRegions : List<UserInfoRegionDto>    = emptyList(),
        val userInterests: List<UserInfoInterestDto> = emptyList()
) {
    constructor(user: User, userRegions: List<UserInfoRegionDto>, userInfoInterests: List<UserInfoInterestDto>): this(
            userName = user.userName ?: "",
            birthday = user.birthday?.toBaseDate()   ?: "",
            profileImageLink = user.profileImageLink ?: "",
            userRegions = userRegions,
            userInterests = userInfoInterests
    )
}