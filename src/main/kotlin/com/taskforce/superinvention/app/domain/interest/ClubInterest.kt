package com.taskforce.superinvention.app.domain.interest

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.Club
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class ClubInterest(
        @ManyToOne
        var club: Club,
        @ManyToOne
        var interest: Interest,
        var priority: Long
) : BaseEntity() {
}