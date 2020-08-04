package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.model.AppToken
import com.taskforce.superinvention.app.web.dto.KakaoTokenDto
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import com.taskforce.superinvention.common.util.KakaoOAuth
import org.junit.Assert.assertEquals

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

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

    @Test
    fun `AppToken 발행 - 신규 가입 유저`() {

        // given
        val kakaoToken = KakaoTokenDto()
        val user: User = User("13141")

        given(kakaoOAuth.getKakaoId(kakaoToken)).willReturn(user.userId)
        given(userRepository.findByUserId(user.userId)).willReturn(null)

        // when
        val appToken: AppToken = userService.publishAppToken(kakaoToken)

        // then
        assertEquals(appToken.isFirst, true)
    }

    @Test
    fun `AppToken 발행 - 기존 로그인 유저`() {

        // given
        val kakaoToken = KakaoTokenDto()
        val user: User = User("13141")
        user.userRoles.add(UserRole(user, "ROLE_USER"))

        `when`(kakaoOAuth.getKakaoId(kakaoToken)).thenReturn(user.userId)
        `when`(userRepository.findByUserId(user.userId)).thenReturn(user)
        `when`(jwtTokenProvider.createAppToken(user.userId, user.userRoles)).thenReturn("hased-mock-token")

        // when
        val appToken: AppToken = userService.publishAppToken(kakaoToken)

        // then
        assertEquals(appToken.isFirst, false)
    }
}