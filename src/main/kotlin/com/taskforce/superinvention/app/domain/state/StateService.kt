package com.taskforce.superinvention.app.domain.state

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class StateService(
        var stateRepositorySupport: StateRepositorySupport
) {
    @Cacheable(cacheNames = arrayOf("cache"))
    fun findAllStateDtoList(): List<StateDto> {
        return stateRepositorySupport.findByLevel(1).map { e -> StateDto(e) }.toList()
    }
}