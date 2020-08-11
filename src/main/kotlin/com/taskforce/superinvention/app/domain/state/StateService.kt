package com.taskforce.superinvention.app.domain.state

import com.taskforce.superinvention.app.web.dto.state.StateDto
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class StateService(
        var stateRepositorySupport: StateRepositorySupport,
        var userStateRepositorySupport: UserStateRepositorySupport
) {
    @Cacheable(cacheNames = arrayOf("cache"))
    fun findAllStateDtoList(): List<StateDto> {
        return stateRepositorySupport.findByLevel(1).map { e -> StateDto(e) }.toList()
    }

    fun findUserStateList(userSeq: Long): List<UserState> {
        return userStateRepositorySupport.findByUserSeq(userSeq)
    }
}