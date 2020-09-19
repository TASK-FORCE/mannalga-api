package com.taskforce.superinvention.app.domain.state

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.Club
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne

@Entity(name = "club_state")
class ClubState(

        @ManyToOne(fetch = FetchType.LAZY)
        var club:Club,

        @ManyToOne(fetch = FetchType.LAZY)
        var state: State,
        var priority: Long
) : BaseEntity()