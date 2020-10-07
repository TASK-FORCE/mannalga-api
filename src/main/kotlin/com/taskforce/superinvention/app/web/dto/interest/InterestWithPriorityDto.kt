package com.taskforce.superinvention.app.web.dto.interest

import com.taskforce.superinvention.app.domain.interest.ClubInterest
import com.taskforce.superinvention.app.domain.interest.interest.InterestDto

class InterestWithPriorityDto(
        val interest: InterestDto,
        val priority: Long
) {
    constructor(clubInterest: ClubInterest): this(InterestDto(clubInterest.interest), clubInterest.priority)
}