package com.taskforce.superinvention.app.web.controller

import com.taskforce.superinvention.app.domain.user.user.User
import com.taskforce.superinvention.app.domain.user.user.UserService
import com.taskforce.superinvention.app.domain.user.userState.UserStateService
import com.taskforce.superinvention.app.model.AppToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserInfo
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserRegistRequest
import com.taskforce.superinvention.app.web.dto.state.StateRequestDto
import com.taskforce.superinvention.app.web.dto.state.UserStateDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
        private val userService: UserService,
        private val userStateService: UserStateService
) {

    @PostMapping("/saveKakaoToken")
    fun saveKakaoToken(@RequestBody token: KakaoToken): AppToken {
        return userService.saveKakaoToken(token)
    }

    @Secured("ROLE_USER", "ROLE_UNREGISTERED")
    @GetMapping("/profile")
    fun getUserInfo(@AuthUser user: User): User {
        return user
    }

    @Secured("ROLE_USER", "ROLE_UNREGISTERED")
    @GetMapping("/kakao-profile")
    fun getKakaoUserInfo(@AuthUser user: User): KakaoUserInfo {
        return userService.getKakaoUserInfo(user)
    }

    @Secured("ROLE_USER", "ROLE_UNREGISTERED")
    @PostMapping("/regist")
    fun registerUser(@RequestBody request: KakaoUserRegistRequest, @AuthUser user: User) {
        userService.registerUser(request, user)
    }

    @Secured("ROLE_USER")
    @GetMapping("/states", "ROLE_UNREGISTERED")
    fun getUserStateList(@AuthUser user: User): UserStateDto? {
        val findUserStateList = userStateService.findUserStateList(user)
        return findUserStateList
    }

    @Secured("ROLE_USER", "ROLE_UNREGISTERED")
    @PutMapping("/states")
    fun changeUserStates(@AuthUser user: User,
                         @RequestBody stateRequestDto: List<StateRequestDto>): UserStateDto {
        return userStateService.changeUserState(user, stateRequestDto)
    }
}

