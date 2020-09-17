package com.taskforce.superinvention.app.web.dto.club

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.user.User

class ClubUserDto(
        club: Club,
        users: List<User>
) {
    var club: Club = club
    var users: List<User> = users
}