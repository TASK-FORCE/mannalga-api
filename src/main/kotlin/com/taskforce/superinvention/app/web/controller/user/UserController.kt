package com.taskforce.superinvention.app.web.controller.user

import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.UserService
import com.taskforce.superinvention.app.model.AppToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserInfo
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserRegistRequest
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
        private val userService: UserService
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
}

