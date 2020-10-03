package com.taskforce.superinvention.app.web.dto.region

import com.taskforce.superinvention.app.domain.user.User

class UserRegionDto(user: User, regions: List<RegionWithPriorityDto>) {
    val userSeq: Long?
    val userId: String
    val userRegions: List<RegionWithPriorityDto>

    init {
        this.userSeq = user.seq
        this.userId  = user.userId
        this.userRegions = regions
    }
}
