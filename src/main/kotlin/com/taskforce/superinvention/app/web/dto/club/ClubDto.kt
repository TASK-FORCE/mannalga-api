package com.taskforce.superinvention.app.web.dto.club

import com.fasterxml.jackson.annotation.JsonProperty
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.user.ClubUser
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
        val userInfo: ClubUserStatusDto?,
        val userList: List<ClubInfoUserDto>
)

data class ClubUserStatusDto(
    val userSeq: Long,
    val clubUserSeq: Long,
    val role: List<Role.RoleName>,
    val isLiked: Boolean
) {
    constructor(clubUser: ClubUser) : this(
        userSeq     = clubUser.user.seq!!,
        clubUserSeq = clubUser.seq!!,
        role        = clubUser.clubUserRoles.map { clubUserRoles -> clubUserRoles.role.name },
        isLiked     = clubUser.isLiked ?: false
    )
}


data class ClubWriter(
    val writerUserSeq: Long,
    val writerClubUserSeq : Long,
    val name  : String,
    val imgUrl: String,
    val role: List<Role.RoleName>
) {
    constructor(writer: ClubUser): this(
        name               = writer.user.userName ?: "",
        writerClubUserSeq  = writer.seq!!,
        writerUserSeq      = writer.user.seq!!,
        imgUrl             = writer.user.profileImageLink ?: "",
        role               = writer.clubUserRoles.map { clubUserRoles -> clubUserRoles.role.name }
    )
}

data class ClubInfoUserDto(
    val clubUserSeq: Long,
    val userSeq: Long,
    val name: String,
    val imgUrl: String,
    val role: List<Role.RoleName>
) {
    constructor(clubUser: ClubUser)
        :this(
            userSeq     = clubUser.user.seq!!,
            clubUserSeq = clubUser.seq!!,
            name   = clubUser.user.userName         ?: "",
            imgUrl = clubUser.user.profileImageLink ?: "",
            role   = clubUser.clubUserRoles.map { clubUserRoles -> clubUserRoles.role.name }
        )
}

data class ClubInfoDto(
    var seq: Long?,
    var name: String,
    var description: String,
    var maximumNumber: Long,
    var userCount: Long?,
    var mainImageUrl: String?,
    var interests: List<InterestWithPriorityDto>,
    var regions: List<RegionWithPriorityDto>
) {
    constructor(
            club : Club,
            clubInterest: List<InterestWithPriorityDto>,
            clubRegion: List<RegionWithPriorityDto>
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
            clubRegion: List<RegionWithPriorityDto>
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
    constructor(club : Club, userCount: Long):
            this(
                    club.seq,
                    club.name,
                    club.description,
                    club.maximumNumber,
                    userCount,
                    club.mainImageUrl,
                    club.clubInterests.map(::InterestWithPriorityDto),
                    club.clubRegions.map(::RegionWithPriorityDto)
            )
}