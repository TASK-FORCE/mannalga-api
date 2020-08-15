package com.taskforce.superinvention.app.domain.state

import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class StateService(
        private var stateRepositorySupport: StateRepositorySupport
) {

    @Cacheable(cacheNames = ["cache"])
    fun findAllStateDtoList(): List<StateDto> {
        return stateRepositorySupport.findByLevel(1).map { e -> StateDto(e) }.toList()
    }
}