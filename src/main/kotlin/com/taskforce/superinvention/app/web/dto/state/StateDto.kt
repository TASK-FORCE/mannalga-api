package com.taskforce.superinvention.app.web.dto.state

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.state.State
import java.util.stream.Collectors


class StateDto(state: State) {
    var seq: Long?
    var name: String
    var superStateRoot: String
    var level: Long
    var subStates: List<StateDto>

    init {
        this.seq = state.seq
        this.name = state.name
        this.superStateRoot = state.superStateRoot
        this.level = state.level
        this.subStates = state.subStates.stream().map { e -> StateDto(e) }.collect(Collectors.toList())
    }
}