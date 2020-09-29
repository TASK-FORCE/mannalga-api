package com.taskforce.superinvention.app.web.dto.region

import com.taskforce.superinvention.app.domain.region.Region
import kotlin.streams.toList


class RegionDto {
    var seq: Long?
    var name: String
    var superRegionRoot: String
    var level: Long
    var subRegions: List<RegionDto>

    constructor(
            seq: Long?,
            name: String,
            superRegionRoot: String,
            level: Long,
            subRegions: List<RegionDto>
    ) {
        this.seq = seq
        this.name = name
        this.superRegionRoot = superRegionRoot
        this.level = level
        this.subRegions = subRegions
    }
}


fun of(region: Region, findDepth: Long): RegionDto
 {
     return RegionDto(
        seq = region.seq,
             name = region.name,
             superRegionRoot = region.superRegionRoot,
             level = region.level,
             subRegions = if (findDepth > 0) region.subRegions.stream().map { e -> of(e, findDepth - 1) }.toList() else ArrayList()
     )
 }