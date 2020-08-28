package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.domain.interest.interest.InterestService
import com.taskforce.superinvention.app.domain.state.StateService
import com.taskforce.superinvention.app.domain.user.userRole.UserRole
import com.taskforce.superinvention.app.domain.user.userRole.UserRoleRepository
import com.taskforce.superinvention.app.model.AppToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoToken
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import com.taskforce.superinvention.common.util.KakaoOAuth
import org.junit.Assert.assertEquals

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.*
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.MockBeans
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.context.annotation.Bean

@ExtendWith(MockitoExtension::class)
class UserServiceTest {

    @InjectMocks
    lateinit var userService: UserService

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var userRoleRepository: UserRoleRepository

    @Mock
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Mock
    lateinit var kakaoOAuth: KakaoOAuth

    @Mock
    lateinit var stateService: StateService

    @Mock
    lateinit var interestService: InterestService

    @Test
    fun `AppToken 발행 - 신규 가입 유저`() {

        // given
        val kakaoToken = KakaoToken()
        val user: User = User("13141")

        `when`(kakaoOAuth.getKakaoUserId(kakaoToken)).thenReturn(user.userId)
        `when`(userRepository.findByUserId(user.userId)).thenReturn(null)
        `when`(jwtTokenProvider.createAppToken(anyString())).thenReturn("example-jwt-token")

        // when
        val appToken: AppToken = userService.saveKakaoToken(kakaoToken)

        // then
        assertEquals(appToken.isRegistered, false)
    }

    @Test
    fun `AppToken 발행 - 기존 로그인 유저`() {

        // given
        val kakaoToken = KakaoToken()
        val user: User = User("13141")
        user.userRoles.add(UserRole(user, "ROLE_USER"))
        user.isRegistered = 1

        `when`(kakaoOAuth.getKakaoUserId(kakaoToken)).thenReturn(user.userId)
        `when`(userRepository.findByUserId(user.userId)).thenReturn(user)
        `when`(jwtTokenProvider.createAppToken(user.userId)).thenReturn("example-jwt-token")

        // when
        val appToken: AppToken = userService.saveKakaoToken(kakaoToken)

        // then
        assertEquals(appToken.isRegistered, true)
    }
}