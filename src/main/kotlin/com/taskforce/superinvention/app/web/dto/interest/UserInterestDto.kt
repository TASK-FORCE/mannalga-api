package com.taskforce.superinvention.app.web.dto.interest

import com.taskforce.superinvention.app.domain.user.User

class UserInterestDto(
    val userSeq: Long,
    val userId: String,
    val interestList: List<InterestWithPriorityDto>
)