package com.taskforce.superinvention.app.web.dto.state

import com.taskforce.superinvention.app.domain.state.State
import kotlin.reflect.KFunction2

class StateWithPriorityDto(
        val stateDto: SimpleStateDto,
        val priority: Long)