package com.taskforce.superinvention.app.domain.state

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.Club
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class ClubState(
        @ManyToOne
        var club: Club,
        @ManyToOne
        var state: State,
        var priority: Long
) : BaseEntity() {
}