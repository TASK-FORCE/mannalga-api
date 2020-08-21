package com.taskforce.superinvention.app.web.dto.state

import com.taskforce.superinvention.app.domain.state.State

class SimpleStateDto(
        val seq: Long,
        val name: String,
        val superStateRoot: String,
        val level: Long
) {


    constructor(state: State): this(state.seq!!, state.name, state.superStateRoot, state.level)
}