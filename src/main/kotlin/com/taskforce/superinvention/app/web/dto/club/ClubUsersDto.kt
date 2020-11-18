package com.taskforce.superinvention.app.web.dto.club

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.role.RoleDto

class ClubUsersDto(
        club: Club,
        users: List<User>
) {
    var club: Club = club
    var users: List<User> = users
}

class ClubUserDto(
        val seq: Long,
        val userSeq: Long,
        val club: ClubDto,
        val roles: Set<RoleDto>
) {
    constructor(clubUser: ClubUser): this(
            seq = clubUser.seq!!,
            userSeq = clubUser.user.seq!!,
            club = ClubDto(clubUser.club),
            roles = clubUser.clubUserRoles.map { e -> RoleDto(e.role) }.toSet()
    )
}


class ClubUserInfoDetail {

}