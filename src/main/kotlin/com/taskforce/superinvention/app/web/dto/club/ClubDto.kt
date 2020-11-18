package com.taskforce.superinvention.app.web.dto.club

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.interest.ClubInterest
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.web.dto.interest.InterestWithPriorityDto
import com.taskforce.superinvention.app.web.dto.region.RegionWithPriorityDto
import com.taskforce.superinvention.app.web.dto.region.SimpleRegionDto

data class ClubDto (
        var seq: Long?,
        var name: String,
        var description: String,
        var maximumNumber: Long,
        var userCount: Long?,
        var mainImageUrl: String?
){
    constructor(club : Club):
            this(   club.seq,
                    club.name,
                    club.description,
                    club.maximumNumber,
                    club.userCount,
                    club.mainImageUrl ?: ""
            )
}

data class ClubInfoDetailsDto(
        val clubInfo: ClubInfoDto,
        val userInfo: ClubUserStatusDto?
)

data class ClubUserStatusDto(
        val role: List<Role.RoleName>,
        val isLiked: Boolean
)

data class ClubInfoDto(
        var seq: Long?,
        var name: String,
        var description: String,
        var maximumNumber: Long,
        var userCount: Long?,
        var mainImageUrl: String?,
        var clubInterest: List<InterestWithPriorityDto>,
        var clubRegion: List<SimpleRegionDto>
) {
    constructor(
            club : Club,
            userCount: Long?,
            clubInterest: List<InterestWithPriorityDto>,
            clubRegion: List<SimpleRegionDto>
    ): this(
            club.seq,
            club.name,
            club.description,
            club.maximumNumber,
            club.userCount,
            club.mainImageUrl ?: "",
            clubInterest,
            clubRegion
    )
    constructor(
            club : ClubDto,
            clubInterest: List<InterestWithPriorityDto>,
            clubRegion: List<SimpleRegionDto>
    ): this(
            club.seq,
            club.name,
            club.description,
            club.maximumNumber,
            club.userCount,
            club.mainImageUrl ?: "",
            clubInterest,
            clubRegion
    )
}


data class ClubWithRegionInterestDto (
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