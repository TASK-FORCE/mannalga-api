package com.taskforce.superinvention.app.domain.club

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.user.user.User
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class ClubUser(
        @ManyToOne
        var club: Club,

        @ManyToOne
        var user: User
) : BaseEntity()