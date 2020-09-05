package com.taskforce.superinvention.app.domain.state

import com.taskforce.superinvention.app.web.dto.state.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StateService(
        val stateRepository: StateRepository
) {
    @Transactional
    fun findAllStateDtoList(): List<StateDto> {
        return stateRepository.findByLevel(1).map { e -> of(e, 1) }.toList()
    }
}