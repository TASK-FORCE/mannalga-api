package com.taskforce.superinvention.app.domain.club

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.user.User
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
class ClubUser(
        @ManyToOne
        var club: Club,

        @ManyToOne
        var user: User
) : BaseEntity() {

}