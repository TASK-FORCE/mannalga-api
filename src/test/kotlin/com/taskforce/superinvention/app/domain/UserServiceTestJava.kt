package com.taskforce.superinvention.app.domain

import com.taskforce.superinvention.app.domain.user.UserService
import com.taskforce.superinvention.app.web.dto.KakaoTokenDto
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class UserServiceTestJava {

    @Autowired
    lateinit var userService: UserService

    @Test
    fun token() {
        val kakaoToken = KakaoTokenDto(
                accessToken = "SO85bHhHnA7TPDbrflULuT16lr_MmGxVaqhuXgo9dZwAAAFzrFpd7A",
                refreshToken = "GzmZu3mL2n0ae44rBuYNJHepVjNKLtSknfQxTgo9dZwAAAFzrFpd6g"
        )

        val result = userService.registerUserWithToken(kakaoToken)
        print(result)
    }
}