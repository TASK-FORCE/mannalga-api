package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

@ExtendWith(SpringExtension::class)
@WebMvcTest(value = [UserController::class])
class UserControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var userService: UserService

    @MockBean
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Test
    @DisplayName("/saveKakaoToken으로 유저가 처음 접근했을 때")
    fun saveKakaoTokenPerform() {
        // given
        val mockRequest= MockMvcRequestBuilders.post("/saveKakaoToken")
        mockMvc.perform(mockRequest)
    }
}