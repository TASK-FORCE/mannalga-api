package com.taskforce.superinvention.app.web

import com.taskforce.superinvention.app.domain.interest.interest.InterestService
import com.taskforce.superinvention.app.domain.state.StateService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.UserService
import com.taskforce.superinvention.app.model.AppToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserInfo
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserRegistRequest
import com.taskforce.superinvention.app.web.dto.state.StateRequestDto
import com.taskforce.superinvention.app.web.dto.state.UserStateDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.RestTemplate
import java.lang.NullPointerException

@RestController
@RequestMapping("/users")
class UserController(
        private val userService: UserService,
        private val stateService: StateService,
        private val interestService: InterestService
) {

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    fun getUserInfo(@AuthUser user: User): User? {
        return user
    }

    @GetMapping("/kakao-profile")
    @PreAuthorize("isAuthenticated()")
    fun getKakaoUserInfo(@AuthUser user: User): KakaoUserInfo {
        return userService.getKakaoUserInfo(user)
    }

    @PostMapping("/saveKakaoToken")
    fun saveKakaoToken(@RequestBody token: KakaoToken): AppToken {
        return userService.publishAppToken(token)
    }

    @PostMapping("/regist")
    @PreAuthorize("isAuthenticated()")
    fun registerUser(@RequestBody request: KakaoUserRegistRequest, @AuthUser user:User) {
        user.birthday = request.birthday
        user.userName = request.userName
        user.profileImageLink = request.profileImageLink

        userService.save(user)

        val userStates = request.userStates
        stateService.changeUserState(user, userStates)
        val userInterests = request.userInterests

        interestService.changeUserInterest(user, userInterests)
    }


    @GetMapping("/states")
    @PreAuthorize("isAuthenticated()")
    fun getUserStateList(@AuthUser user: User): UserStateDto {
        val findUserStateList = stateService.findUserStateList(user)
        return findUserStateList
    }

    @PutMapping("/states")
    @PreAuthorize("isAuthenticated()")
    fun changeUserStates(@AuthUser user: User, @RequestBody stateRequestDto: List<StateRequestDto>): UserStateDto {
        return stateService.changeUserState(user, stateRequestDto)
    }
}

