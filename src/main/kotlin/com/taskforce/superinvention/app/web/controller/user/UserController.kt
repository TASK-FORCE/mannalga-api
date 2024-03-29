package com.taskforce.superinvention.app.web.controller.user

import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.UserInfoService
import com.taskforce.superinvention.app.domain.user.UserService
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.kakao.KakaoToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserInfo
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserRegistRequest
import com.taskforce.superinvention.app.web.dto.user.UserIdAndNameDto
import com.taskforce.superinvention.app.web.dto.user.UserMemberCheckDto
import com.taskforce.superinvention.app.web.dto.user.UserProfileUpdateDto
import com.taskforce.superinvention.app.web.dto.user.info.UserInfoDto
import com.taskforce.superinvention.common.config.argument.resolver.auth.AuthUser
import com.taskforce.superinvention.common.config.security.AppToken
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import com.taskforce.superinvention.common.exception.BizException
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users")
class UserController(
        private val userService: UserService,
        private val userInfoService: UserInfoService,
        private val jwtTokenProvider: JwtTokenProvider
) {

    @Value("\${spring.profiles.active}")
    lateinit var profile: String

    @PostMapping("/saveKakaoToken")
    fun saveKakaoToken(@RequestBody token: KakaoToken): ResponseDto<AppToken> {
        return ResponseDto(data = userService.saveKakaoToken(token))
    }

    @Secured(Role.NONE, Role.MEMBER)
    @GetMapping("/profile")
    fun getUserInfo(@AuthUser user: User): ResponseDto<UserInfoDto> {
        return ResponseDto(data = userInfoService.getUserInfo(user))
    }

    @GetMapping("/check-already-register")
    fun checkMember(@AuthUser user: User): ResponseDto<UserMemberCheckDto> {
        return ResponseDto(data = UserMemberCheckDto(user.isRegistered ?: false))
    }

    @GetMapping("/kakao-profile")
    fun getKakaoUserInfo(@AuthUser user: User): ResponseDto<KakaoUserInfo> {
        return ResponseDto(data = userService.getKakaoUserInfo(user))
    }

    @PostMapping("/regist")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerUser(@AuthUser user: User,
                     @RequestBody request: KakaoUserRegistRequest): ResponseDto<String> {

        userService.registerUser(request, user)
        return ResponseDto(data = ResponseDto.EMPTY)
    }

    @Secured(Role.MEMBER)
    @PatchMapping
    fun changeUserProfile(@AuthUser user: User,
                          @RequestBody body: UserProfileUpdateDto): ResponseDto<UserInfoDto> {

        userService.updateUser(user, body)
        return ResponseDto(data = userInfoService.getUserInfo(user))
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/withdraw")
    @Secured(Role.MEMBER)
    fun withdrawMember(@AuthUser user: User): ResponseDto<String> {
        userService.withdraw(user)
        return ResponseDto(ResponseDto.EMPTY)
    }

    /**
     * Use Only Develop Profile!!
     * find User token by username.
     */
    @GetMapping("/door/{username}")
    fun backdoorUserToken(@PathVariable username: String): ResponseDto<String> {
        checkDevProfile()
        var user = userService.getUserByUsername(username)
        val appToken = jwtTokenProvider.createAppToken(user.userId)

        return ResponseDto(appToken)
    }

    @GetMapping("/door")
    fun backdoorUserList(): ResponseDto<List<UserIdAndNameDto>> {
        checkDevProfile()
        return ResponseDto(userService.getUserIdAndUserNameList())
    }

    @GetMapping("/door-seq/{userSeq}")
    fun backdoorUserTokenForUserSeq(@PathVariable userSeq: Long): ResponseDto<String> {
        checkDevProfile()
        var user = userService.getUserBySeq(userSeq)
        val appToken = jwtTokenProvider.createAppToken(user.userId)
        return ResponseDto(appToken)
    }

    private fun checkDevProfile() {
        if (!profile.contains("dev")) throw BizException("개발서버에서만 가능한 동작입니다.")
    }
}

