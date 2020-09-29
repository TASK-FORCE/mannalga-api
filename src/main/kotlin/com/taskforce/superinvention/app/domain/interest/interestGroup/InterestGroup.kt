package com.taskforce.superinvention.app.domain.interest.interestGroup

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.interest.interest.Interest
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany

@Entity
class InterestGroup (
        var name: String,

        @OneToMany(mappedBy = "interestGroup", fetch = FetchType.LAZY)
        var interesList: List<Interest>
) : BaseEntity()