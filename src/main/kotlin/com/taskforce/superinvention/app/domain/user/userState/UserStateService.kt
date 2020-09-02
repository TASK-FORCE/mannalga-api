package com.taskforce.superinvention.app.domain.user.userState

import com.taskforce.superinvention.app.domain.state.StateRepository
import com.taskforce.superinvention.app.domain.user.user.User
import com.taskforce.superinvention.app.web.dto.state.SimpleStateDto
import com.taskforce.superinvention.app.web.dto.state.StateRequestDto
import com.taskforce.superinvention.app.web.dto.state.StateWithPriorityDto
import com.taskforce.superinvention.app.web.dto.state.UserStateDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserStateService(
        val userStateRepository: UserStateRepository,
        val stateRepository: StateRepository
) {

    fun findUserStateList(user: User): UserStateDto {
        val userStates = userStateRepository.findByUserSeq(user.seq!!)

        return when(userStates.isEmpty()) {
            true -> UserStateDto(user, emptyList())
            else -> UserStateDto(user, userStates.map { e -> StateWithPriorityDto(SimpleStateDto(e.state), e.priority) }.toList())
        }
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