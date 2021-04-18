package com.taskforce.superinvention.app.web.dto.region

import com.taskforce.superinvention.app.domain.region.ClubRegion
import com.taskforce.superinvention.app.domain.region.Region

class SimpleRegionDto(
        val seq: Long,
        val name: String,
        val superRegionRoot: String,
        val level: Long
) {


    constructor(region: Region): this(region.seq!!, region.name, region.superRegionRoot, region.level)

    constructor(clubRegion: ClubRegion):
            this(
                    clubRegion.region.seq!!,
                    clubRegion.region.name,
                    clubRegion.region.superRegionRoot,
                    clubRegion.region.level
            )
}