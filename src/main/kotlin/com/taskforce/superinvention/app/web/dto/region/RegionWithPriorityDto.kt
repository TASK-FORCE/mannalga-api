package com.taskforce.superinvention.app.web.dto.region

import com.taskforce.superinvention.app.domain.region.ClubRegion

class RegionWithPriorityDto(
        val region: SimpleRegionDto,
        val priority: Long
) {
    constructor(clubRegion: ClubRegion): this(SimpleRegionDto(clubRegion.region), clubRegion.priority)
}