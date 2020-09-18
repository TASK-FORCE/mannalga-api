package com.taskforce.superinvention.app.domain.club

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.user.User
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class ClubUser(
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_seq")
        var club: Club,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "club_seq")
        var user: User
) : BaseEntity()