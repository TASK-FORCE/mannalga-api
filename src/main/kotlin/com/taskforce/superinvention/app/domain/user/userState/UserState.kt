package com.taskforce.superinvention.app.domain.user.userState

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.state.State
import com.taskforce.superinvention.app.domain.user.User
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class UserState(
        @ManyToOne
        var user: User,
        @ManyToOne
        var state: State,
        var priority: Long
) : BaseEntity()