package com.taskforce.superinvention.app.web.dto.state

import com.taskforce.superinvention.app.domain.state.State
import java.util.stream.Collectors


class StateDto {
    var seq: Long?
    var name: String
    var superStateRoot: String
    var level: Long
    var subStates: List<StateDto>


    constructor(state: State) {
        this.seq = state.seq
        this.name = state.name
        this.superStateRoot = state.superStateRoot
        this.level = state.level
        this.subStates = state.subStates.stream().map { e -> StateDto(e) }.collect(Collectors.toList())
    }

    constructor(
            seq: Long?,
            name: String,
            superStateRoot: String,
            level: Long,
            subStates: List<StateDto>
    ) {
        this.seq = seq
        this.name = name
        this.superStateRoot = superStateRoot
        this.level = level
        this.subStates = subStates
    }
}