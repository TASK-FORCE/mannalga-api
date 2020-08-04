package com.taskforce.superinvention.common.security

import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.UserDetailsService
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import com.taskforce.superinvention.common.config.security.SecurityUser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class JwtTokenPrividerTest {

    @InjectMocks
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Mock
    lateinit var userDetailsService: UserDetailsService

    @Test
    fun tokenTest() {

        // given
        val user  = User("test-user-id")
        val token = "test-kakao-oauth-token"
        val securityUser = SecurityUser(user, token)
        val appToken: String = jwtTokenProvider.createAppToken(user.userId, user.userRoles)

        `when`(userDetailsService.loadUserByUsername(user.userId)).thenReturn(securityUser)

        // when
        val authentication = jwtTokenProvider.getAuthentication(appToken)
        val loginUser = authentication.principal as SecurityUser

        // then
        assertNotEquals(loginUser.username, securityUser.user.userId)
        assertEquals(loginUser.user.userId, user.userId)
    }
}