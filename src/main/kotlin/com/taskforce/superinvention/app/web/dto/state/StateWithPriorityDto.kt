package com.taskforce.superinvention.app.web.dto.state

import com.taskforce.superinvention.app.domain.state.ClubState

class StateWithPriorityDto(
        val state: SimpleStateDto,
        val priority: Long
) {
    constructor(clubState: ClubState): this(SimpleStateDto(clubState.state), clubState.priority)
}