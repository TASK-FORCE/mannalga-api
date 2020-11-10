package com.taskforce.superinvention.app.domain.interest

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.interest.interest.Interest
import com.taskforce.superinvention.app.domain.interest.interestGroup.InterestGroup
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class ClubInterest(
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "club_seq")
        var club: Club,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "interest_seq")
        var interest: Interest,
        var priority: Long
) : BaseEntity() {
}