package com.taskforce.superinvention.app.web.dto.region

import com.taskforce.superinvention.app.domain.region.Region
import kotlin.streams.toList


class RegionDto {
    var seq: Long?
    var name: String
    var superStateRoot: String
    var level: Long
    var subStates: List<RegionDto>

    constructor(
            seq: Long?,
            name: String,
            superStateRoot: String,
            level: Long,
            subStates: List<RegionDto>
    ) {
        this.seq = seq
        this.name = name
        this.superStateRoot = superStateRoot
        this.level = level
        this.subStates = subStates
    }
}


fun of(region: Region, findDepth: Long): RegionDto
 {
     return RegionDto(
        seq = region.seq,
             name = region.name,
             superStateRoot = region.superRegionRoot,
             level = region.level,
             subStates = if (findDepth > 0) region.subRegions.stream().map { e -> of(e, findDepth - 1) }.toList() else ArrayList()
     )
 }