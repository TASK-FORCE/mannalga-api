package com.taskforce.superinvention.app.web.user

import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.UserService
import com.taskforce.superinvention.app.model.AppToken
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.kakao.KakaoToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserInfo
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserRegistRequest
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
        private val userService: UserService
) {

    @PostMapping("/saveKakaoToken")
    fun saveKakaoToken(@RequestBody token: KakaoToken): ResponseDto<AppToken> {
        return ResponseDto(data = userService.saveKakaoToken(token))
    }

    @Secured(Role.NONE, Role.MEMBER)
    @GetMapping("/profile")
    fun getUserInfo(@AuthUser user: User): ResponseDto<User> {
        return ResponseDto(data = user)
    }

    @Secured(Role.NONE, Role.MEMBER)
    @GetMapping("/kakao-profile")
    fun getKakaoUserInfo(@AuthUser user: User): ResponseDto<KakaoUserInfo> {
        return ResponseDto(data = userService.getKakaoUserInfo(user))
    }

    @Secured(Role.NONE, Role.MEMBER)
    @PostMapping("/regist")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@AuthUser user: User,
                     @RequestBody request: KakaoUserRegistRequest): ResponseDto<Any> {

        userService.registerUser(request, user)
        return ResponseDto(data = ResponseDto.EMPTY)
    }
}

