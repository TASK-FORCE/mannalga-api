package com.taskforce.superinvention.app.web.dto.state

import com.taskforce.superinvention.app.domain.user.user.User

class UserStateDto(user: User, states: List<StateWithPriorityDto>) {
    val userSeq: Long?
    val userId: String
    val userStates: List<StateWithPriorityDto>

    init {
        this.userSeq = user.seq
        this.userId  = user.userId
        this.userStates = states
    }
}
