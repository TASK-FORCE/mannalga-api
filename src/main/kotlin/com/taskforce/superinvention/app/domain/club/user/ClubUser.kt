package com.taskforce.superinvention.app.domain.club.user

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.role.ClubUserRole
import com.taskforce.superinvention.app.domain.user.User
import javax.persistence.*

@Entity
class ClubUser(
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "club_seq")
        var club: Club,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_seq")
        var user: User,

        var isLiked: Boolean?
) : BaseEntity() {

        constructor(club: Club, user: User)
                : this(club, user, false)


        @OneToMany
        @JoinColumn(name = "club_user_seq")
        lateinit var clubUserRoles: MutableSet<ClubUserRole>
}