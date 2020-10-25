package com.taskforce.superinvention.app.domain.user.userInterest

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.interest.interest.Interest
import com.taskforce.superinvention.app.domain.user.User
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class UserInterest(
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_seq")
        var user: User,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "interest_seq")
        var interest: Interest,
        var priority: Long
) : BaseEntity()