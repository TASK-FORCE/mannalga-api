package com.taskforce.superinvention.app.domain.interest

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.interest.interest.Interest
import com.taskforce.superinvention.app.domain.user.User
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class UserInterest(
    @ManyToOne
    var user: User,
    @ManyToOne
    var interest: Interest,
    var priority: Long
) : BaseEntity()