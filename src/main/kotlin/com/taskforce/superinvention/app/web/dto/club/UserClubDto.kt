package com.taskforce.superinvention.app.web.dto.club

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.user.User
import javax.swing.plaf.nimbus.State

class UserClubDto(
        user: User,
        clubs: List<Club>
) {
    var user: User = user
    var clubs: List<Club> = clubs
}