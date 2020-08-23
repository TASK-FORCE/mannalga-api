package com.taskforce.superinvention.app.domain.state

import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.state.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class StateService(
        var userStateRepository: UserStateRepository,
        var stateRepository: StateRepository
) {
    fun findAllStateDtoList(): List<StateDto> {
        return stateRepository.findByLevel(1).map { e -> StateDto(e) }.toList()
    }

    fun findUserStateList(user: User): UserStateDto {
        val userStates = userStateRepository.findByUserSeq(user.seq!!)
        return UserStateDto(userStates[0].user, userStates.map { e -> StateWithPriorityDto(SimpleStateDto(e.state), e.priority) }.toList())
    }

    @Transactional
    fun changeUserState(user: User, states: List<StateRequestDto>): UserStateDto {
        if (user.seq == null) throw NullPointerException()
        val findByUserSeq: List<UserState> = userStateRepository.findByUserSeq(user.seq!!)
        userStateRepository.deleteAll(findByUserSeq)

        val toAdd = states.map { e -> UserState(user, stateRepository.findById(e.seq).orElseThrow { NullPointerException() }, e.priority) }.toMutableList()
        userStateRepository.saveAll(toAdd)
        return findUserStateList(user)
    }
}