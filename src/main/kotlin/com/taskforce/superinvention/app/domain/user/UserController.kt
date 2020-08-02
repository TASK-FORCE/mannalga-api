package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.web.dto.AppToken
import com.taskforce.superinvention.app.web.dto.KakaoTokenDto
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest

@RestController
class UserController(
        private val userService: UserService,
        private val jwtTokenProvider: JwtTokenProvider
) {

    @PostMapping("/login")
    fun userSignin() {

    }

    @PostMapping("/saveKakaoToken")
    fun saveKakaoToken(@RequestBody token: KakaoTokenDto): AppToken {
        return userService.registerUserWithToken(token)
    }

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    fun getUserInfo(request: HttpServletRequest): User? {
        val token  = jwtTokenProvider.resolveToken(request)
        val userId = jwtTokenProvider.getUserId(token)

        return userService.getUserInfo(userId)
    }

    @PostMapping("/register")
    fun registerUser() {

    }
}