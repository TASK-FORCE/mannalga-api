package com.taskforce.superinvention.app.web.dto.interest

import com.taskforce.superinvention.app.domain.interest.ClubInterest
import com.taskforce.superinvention.app.domain.interest.interest.InterestDto
import com.taskforce.superinvention.app.domain.user.userInterest.UserInterest

data class InterestWithPriorityDto(
        val interest: InterestDto,
        val priority: Long
) {
    constructor(clubInterest: ClubInterest): this(InterestDto(clubInterest.interest), clubInterest.priority)
    constructor(userInterest: UserInterest): this(InterestDto(userInterest.interest), userInterest.priority)
}