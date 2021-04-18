package com.taskforce.superinvention.app.web.dto.interest

class UserInterestDto(
    val userSeq: Long,
    val userId: String?,
    val interestList: List<InterestWithPriorityDto>
)