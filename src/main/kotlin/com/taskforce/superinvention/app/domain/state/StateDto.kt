package com.taskforce.superinvention.app.domain.state

import com.taskforce.superinvention.app.domain.BaseEntity
import java.util.stream.Collectors


class StateDto(state: State) : BaseEntity() {
    var name: String = state.name
    var superStateRoot: String
    var level: Long
    var subStates: List<StateDto>

    init {
        this.superStateRoot = state.superStateRoot
        this.level = state.level
        this.subStates = state.subStates.stream().map { e -> StateDto(e) }.collect(Collectors.toList())
    }
}