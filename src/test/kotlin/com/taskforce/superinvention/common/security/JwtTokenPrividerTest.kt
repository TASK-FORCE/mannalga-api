package com.taskforce.superinvention.common.security

import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.UserDetailsService
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
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
        val user = User("12312")
        given(userDetailsService.loadUserByUsername(user.userId))

        // when

        // then
    }
}