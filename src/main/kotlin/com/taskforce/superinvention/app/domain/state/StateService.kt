package com.taskforce.superinvention.app.domain.state

import com.taskforce.superinvention.app.web.dto.state.*
import org.springframework.stereotype.Service

@Service
class StateService(
        val stateRepository: StateRepository
) {
    fun findAllStateDtoList(): List<StateDto> {
        return stateRepository.findByLevel(1).map { e -> StateDto(e) }.toList()
    }
}