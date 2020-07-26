package com.taskforce.superinvention.app.domain.interest

import com.taskforce.superinvention.app.domain.BaseEntity
import javax.persistence.Entity

@Entity
class InterestGroup(
        var name: String
) : BaseEntity() {
}