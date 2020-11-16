package com.taskforce.superinvention.app.web.controller.user

import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.UserInfoService
import com.taskforce.superinvention.app.domain.user.UserService
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.kakao.KakaoToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserInfo
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserRegistRequest
import com.taskforce.superinvention.app.web.dto.user.UserMemberCheckDto
import com.taskforce.superinvention.app.web.dto.user.info.UserInfoDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import com.taskforce.superinvention.common.config.security.AppToken
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
        private val userService: UserService,
        private val userInfoService: UserInfoService
) {


    @PostMapping("/saveKakaoToken")
    fun saveKakaoToken(@RequestBody token: KakaoToken): ResponseDto<AppToken> {
        return ResponseDto(data = userService.saveKakaoToken(token))
    }

    @Secured(Role.NONE, Role.MEMBER)
    @GetMapping("/profile")
    fun getUserInfo(@AuthUser user: User): ResponseDto<UserInfoDto> {
        return ResponseDto(data = userInfoService.getUserInfo(user))
    }

    @Secured(Role.NONE, Role.MEMBER)
    @GetMapping("/check-already-register")
    fun checkMember(@AuthUser user: User): ResponseDto<UserMemberCheckDto> {
        return ResponseDto(data = UserMemberCheckDto(user.isRegistered ?: false))
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
                     @RequestBody request: KakaoUserRegistRequest): ResponseDto<String> {

        userService.registerUser(request, user)
        return ResponseDto(data = ResponseDto.EMPTY)
    }
}

