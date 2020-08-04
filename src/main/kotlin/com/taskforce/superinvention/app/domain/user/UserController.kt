package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.model.AppToken
import com.taskforce.superinvention.app.web.dto.KakaoTokenDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import com.taskforce.superinvention.common.config.security.SecurityUser
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
        private val userService: UserService
) {

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    fun getUserInfo(@AuthUser user: User): String? {
//        var test = userService.getUserInfo(auth.user.userId)
        return ""
    }

    @PostMapping("/saveKakaoToken")
    fun saveKakaoToken(@RequestBody token: KakaoTokenDto): AppToken {
        return userService.publishAppToken(token)
    }

    @PostMapping("/register")
    fun registerUser() {

    }
}