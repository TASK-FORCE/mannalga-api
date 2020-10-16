package com.taskforce.superinvention.app.web.dto.user.info

import com.taskforce.superinvention.app.domain.interest.interestGroup.InterestGroup
import com.taskforce.superinvention.app.domain.user.userInterest.UserInterest


data class UserInfoInterestDto (
        val interest: UserInfoInterestItem,
        val priority: Long
) {
    constructor(userInterest: UserInterest):
            this(
                    interest = UserInfoInterestItem(userInterest),
                    priority = userInterest.priority
            )
}

data class UserInfoInterestItem (
        val seq : Long,
        val name: String = "",
        val interestGroup: UserInfoInterestGroupDto
) {
    constructor(userInterest: UserInterest):
            this(
                    seq  = userInterest.seq!!,
                    name = userInterest.interest.name,
                    interestGroup = UserInfoInterestGroupDto(userInterest.interest.interestGroup)
            )
}

data class UserInfoInterestGroupDto (
        val seq : Long,
        val name: String
) {
    constructor(interestGroup: InterestGroup) :
            this(
                    seq = interestGroup.seq!!,
                    name= interestGroup.name
            )
}
