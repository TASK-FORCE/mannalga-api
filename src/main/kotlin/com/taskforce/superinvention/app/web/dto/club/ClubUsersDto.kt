package com.taskforce.superinvention.app.web.dto.club

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.role.ClubUserRole
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.role.RoleDto
import java.time.LocalDateTime

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
)