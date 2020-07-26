package com.taskforce.superinvention.app.domain.interest

import com.taskforce.superinvention.app.domain.BaseEntity
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class Interest(
    var name: String,
    var sequence: Long,
    @ManyToOne
    var interestGroup: InterestGroup
) : BaseEntity() {
}