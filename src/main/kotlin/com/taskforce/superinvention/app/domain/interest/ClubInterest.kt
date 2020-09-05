package com.taskforce.superinvention.app.domain.interest

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.interest.interest.Interest
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne

@Entity(name = "club_interest")
class ClubInterest(
        @ManyToOne(fetch = FetchType.LAZY)
        var club: Club,
        @ManyToOne(fetch = FetchType.LAZY)
        var interest: Interest,
        var priority: Long
) : BaseEntity() {
}