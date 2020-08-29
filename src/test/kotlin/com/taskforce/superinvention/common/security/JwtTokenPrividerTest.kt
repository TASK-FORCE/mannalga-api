package com.taskforce.superinvention.common.security

import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.UserDetailsProvider
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import groovy.util.GroovyTestCase.assertEquals
import org.junit.Test
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
    lateinit var userDetailsProvider: UserDetailsProvider

//    @Test
//    fun `사용자 토큰 발급 로직`() {
//
//        // given
//        val user  = User("test-user-id")
//        val appToken: String = jwtTokenProvider.createAppToken(user.userId)
//
//        `when`(userDetailsProvider.loadUserByUsername(user.userId)).thenReturn(user)
//
//        // when
//        val authentication = jwtTokenProvider.getAuthentication(appToken)
//        val loginUser = authentication.principal as User
//
//        // then
//        assertEquals(loginUser.userId, user.userId)
//    }
}