package com.taskforce.superinvention.app.web.dto.user.info

import com.taskforce.superinvention.app.web.dto.region.SimpleRegionDto

data class UserInfoRegionDto (
        val region  : SimpleRegionDto,
        val priority: Long
) {
    constructor(priority: Long, simpleRegionDto: SimpleRegionDto): this (
            region   = simpleRegionDto,
            priority = priority
    )
}