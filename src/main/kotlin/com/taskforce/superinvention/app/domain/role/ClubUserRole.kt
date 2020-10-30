package com.taskforce.superinvention.app.domain.role

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import javax.persistence.*

@Entity
class ClubUserRole(
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "club_user_seq")
        var clubUser: ClubUser,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "role_seq")
        var role: Role
): BaseEntity()