package com.taskforce.superinvention.app.web

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
@RequestMapping("users")
class UserController(
        private val userService: UserService,
        private val stateService: StateService
) {

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    fun getUserInfo(@AuthUser user: User): String? {
//        var test = userService.getUserInfo(auth.user.userId)
        return ""
    }

    @PostMapping("/saveKakaoToken")
    fun saveKakaoToken(@RequestBody token: KakaoToken): AppToken {
        return userService.publishAppToken(token)
    }

    @PostMapping("/regist")
    fun registerUser(@RequestBody request: KakaoUserRegistRequest): ResponseEntity<User> {
        val kakaoToken: KakaoToken = request.kakaoToken
        val user: User =  User(request.kakaoUserid, kakaoToken)
        userService.regist(user);

        return ResponseEntity.ok(user)
    }

    @GetMapping("/states/user/{userSeq}")
    fun getUserStateList(@PathVariable("userSeq") userSeq: Long): UserStateDto {
        return stateService.findUserStateList(userSeq)
    }
}

