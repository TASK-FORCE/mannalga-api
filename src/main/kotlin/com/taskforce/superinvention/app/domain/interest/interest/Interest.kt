package com.taskforce.superinvention.app.domain.interest.interest

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.interest.interestGroup.InterestGroup
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class Interest(
        var name: String,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "interest_group_seq")
        var interestGroup: InterestGroup
) : BaseEntity()