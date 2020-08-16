package com.taskforce.superinvention.app.web.dto.state

import com.taskforce.superinvention.app.domain.user.User

class UserStateDto(user: User, states: List<StateDto>) {
    var userSeq: Long?
    var userId: String
    var states: List<StateDto>

    init {
        this.userSeq = user.seq
        this.userId = user.userId
        this.states = states
    }
}