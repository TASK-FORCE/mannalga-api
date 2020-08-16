package com.taskforce.superinvention.app.web

import com.taskforce.superinvention.app.domain.interest.interest.InterestService
import com.taskforce.superinvention.app.domain.state.StateService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.UserService
import com.taskforce.superinvention.app.model.AppToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserRegistRequest
import com.taskforce.superinvention.app.web.dto.state.UserStateDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

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

    @PostMapping("/saveKakaoToken")
    fun saveKakaoToken(@RequestBody token: KakaoToken): AppToken {
        return userService.publishAppToken(token)
    }

    @PostMapping("/regist")
    @PreAuthorize("isAuthenticated()")
    fun registerUser(@RequestBody request: KakaoUserRegistRequest, @AuthUser user:User): ResponseEntity<User> {
        user.birthday = request.birthday
        user.userName = request.userName
        user.profileImageLink = request.profileImageLink

        userService.save(user)

        val userStates = request.userStates
        stateService.changeUserState(user, userStates)
        val userInterests = request.userInterests

        interestService.changeUserInterest(user, userInterests)

        return ResponseEntity.ok(user)
    }


    @GetMapping("/states/user/{userSeq}")
    fun getUserStateList(@PathVariable("userSeq") userSeq: Long): UserStateDto {
        return stateService.findUserStateList(userSeq)
    }
}

