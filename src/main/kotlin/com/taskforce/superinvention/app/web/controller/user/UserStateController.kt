package com.taskforce.superinvention.app.web.controller.user

import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.userState.UserStateService
import com.taskforce.superinvention.app.web.dto.state.StateRequestDto
import com.taskforce.superinvention.app.web.dto.state.UserStateDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/states")
class UserStateController(
        private val userStateService: UserStateService
) {
    @Secured("ROLE_USER", "ROLE_UNREGISTERED")
    @GetMapping
    fun getUserStateList(@AuthUser user: User): UserStateDto? {
        val findUserStateList = userStateService.findUserStateList(user)
        return findUserStateList
    }

    @Secured("ROLE_USER", "ROLE_UNREGISTERED")
    @PutMapping
    fun changeUserStates(@AuthUser user: User,
                         @RequestBody stateRequestDto: List<StateRequestDto>): UserStateDto {
        return userStateService.changeUserState(user, stateRequestDto)
    }
}

