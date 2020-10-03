package com.taskforce.superinvention.app.web.dto.club

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.web.dto.interest.InterestWithPriorityDto
import com.taskforce.superinvention.app.web.dto.region.RegionWithPriorityDto

class ClubDto (
        var seq: Long?,
        var name: String,
        var description: String,
        var maximumNumber: Long,
        var userCount: Long,
        var mainImageUrl: String?
){
    constructor(club : Club, userCount: Long): this(club.seq, club.name, club.description, club.maximumNumber, userCount, club.mainImageUrl)
}

class ClubWithRegionInterestDto (
        var seq: Long?,
        var name: String,
        var description: String,
        var maximumNumber: Long,
        var userCount: Long,
        var mainImageUrl: String?,
        var interests: List<InterestWithPriorityDto>,
        var regions: List<RegionWithPriorityDto>
) {
    constructor(club : Club,
                userCount: Long):
            this(
                    club.seq,
                    club.name,
                    club.description,
                    club.maximumNumber,
                    userCount,
                    club.mainImageUrl,
                    club.clubInterests.map { e -> InterestWithPriorityDto(e) },
                    club.clubRegions.map { e -> RegionWithPriorityDto(e) }
            )
}