package com.taskforce.superinvention.app.web.dto.state

import com.taskforce.superinvention.app.domain.state.State
import kotlin.streams.toList


class StateDto {
    var seq: Long?
    var name: String
    var superStateRoot: String
    var level: Long
    var subStates: List<StateDto>

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


fun of(state: State, findDepth: Long): StateDto
 {
     return StateDto(
        seq = state.seq,
             name = state.name,
             superStateRoot = state.superStateRoot,
             level = state.level,
             subStates = if (findDepth > 0) state.subStates.stream().map { e -> of(e, findDepth - 1) }.toList() else ArrayList()
     )
 }