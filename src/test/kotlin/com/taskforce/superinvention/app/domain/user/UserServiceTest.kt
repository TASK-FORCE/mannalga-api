package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.domain.user.userInterest.UserInterestService
import com.taskforce.superinvention.app.domain.user.userRole.UserRole
import com.taskforce.superinvention.app.domain.user.userRole.UserRoleService
import com.taskforce.superinvention.app.domain.user.userRegion.UserRegionService
import com.taskforce.superinvention.app.web.dto.kakao.*
import com.taskforce.superinvention.common.config.security.AppToken
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import com.taskforce.superinvention.common.util.aws.s3.AwsS3Mo
import com.taskforce.superinvention.common.util.kakao.KakaoOAuth
import com.taskforce.superinvention.config.test.MockTest
import org.junit.jupiter.api.Assertions.assertEquals

import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`

class UserServiceTest: MockTest() {

    @InjectMocks
    lateinit var userService: UserService

    @Mock
    lateinit var userRepository: UserRepository

    @Mock
    lateinit var userRoleService: UserRoleService

    @Mock
    lateinit var userRegionService: UserRegionService

    @Mock
    lateinit var userInterestService: UserInterestService

    @Mock
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Mock
    lateinit var kakaoOAuth: KakaoOAuth

    @Mock
    lateinit var awsS3Mo: AwsS3Mo

    @Test
    fun `AppToken 발행 - 신규 가입 유저`() {

        // given
        val kakaoToken = KakaoToken()
        val kakaoUserInfo = KakaoUserInfo(
                id = "13141",
                properties = KakaoUserProperties(
                    nickname = "nickname",
                    profile_image = "",
                    thumbnail_image =""

                ),
                kakao_account = KakaoUserAccount(
                    profile_needs_agreement = true,
                    hasGender = true,
                    gender_needs_agreement = true,
                    profile = KakaoUserProfile(
                        nickname = "nickname",
                        profile_image_url   = "" ,
                        thumbnail_image_url = ""
                    )
                )
        )

        val user: User = User("13141")
        user.userRoles.add(UserRole(user, "ROLE_UNREGISTERED"))

        `when`(kakaoOAuth.refreshIfTokenExpired(kakaoToken)).thenReturn(kakaoToken)
        `when`(kakaoOAuth.getKakaoUserProfile(kakaoToken)).thenReturn(kakaoUserInfo)
        `when`(userRepository.findByUserId(user.userId)).thenReturn(null)   // user not registered and not logined
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
        val kakaoUserInfo = KakaoUserInfo(
                id = "13141",
                properties = KakaoUserProperties(
                        nickname = "nickname",
                        profile_image = "",
                        thumbnail_image =""

                ),
                kakao_account = KakaoUserAccount(
                        profile_needs_agreement = true,
                        hasGender = true,
                        gender_needs_agreement = true,
                        profile = KakaoUserProfile(
                                nickname = "nickname",
                                profile_image_url   = "" ,
                                thumbnail_image_url = ""
                        )
                )
        )

        val user: User = User("13141")
        user.userRoles.add(UserRole(user, "ROLE_USER"))
        user.isRegistered = true

        `when`(kakaoOAuth.refreshIfTokenExpired(kakaoToken)).thenReturn(kakaoToken)
        `when`(kakaoOAuth.getKakaoUserProfile(kakaoToken)).thenReturn(kakaoUserInfo)
        `when`(userRepository.findByUserId(user.userId)).thenReturn(user)       // user loged in and registered
        `when`(jwtTokenProvider.createAppToken(user.userId)).thenReturn("example-jwt-token")

        // when
        val appToken: AppToken = userService.saveKakaoToken(kakaoToken)

        // then
        assertEquals(appToken.isRegistered, true)
    }
}