package com.taskforce.superinvention.app.web.dto.club

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.interest.InterestWithPriorityDto
import com.taskforce.superinvention.app.web.dto.region.SimpleRegionDto
import com.taskforce.superinvention.app.web.dto.role.RoleDto

data class ClubUsersDto(
        val club: Club,
        val users: List<User>
)

data class ClubUserDto(
        val seq: Long,
        val userSeq: Long,
        val club: ClubDto,
        val roles: Set<RoleDto>
) {
    constructor(clubUser: ClubUser): this(
            seq = clubUser.seq!!,
            userSeq = clubUser.user.seq!!,
            club = ClubDto(clubUser.club, null),
            roles = clubUser.clubUserRoles.map { e -> RoleDto(e.role) }.toSet()
    )
}

data class ClubUserWithClubDetailsDto(
        val clubUserSeq: Long,
        val userSeq: Long,
        val club: ClubInfoDto,
        val roles: Set<RoleDto>
) {
    constructor(
            clubUserDto: ClubUserDto,
            interests: List<InterestWithPriorityDto>,
            regions: List<SimpleRegionDto>
    ) :this(
            clubUserSeq = clubUserDto.seq,
            userSeq     = clubUserDto.userSeq,
            club        = ClubInfoDto(
                    club = clubUserDto.club,
                    userCount = clubUserDto.club.userCount,
                    clubInterest = interests,
                    clubRegion = regions
            ),
            roles = clubUserDto.roles
        )
}
