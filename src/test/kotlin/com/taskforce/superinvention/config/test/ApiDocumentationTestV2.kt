package com.taskforce.superinvention.config.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.taskforce.superinvention.app.domain.user.*
import com.taskforce.superinvention.app.web.controller.club.ClubController
import com.taskforce.superinvention.common.config.argument.auth.AuthorizeArgumentResolver
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import io.mockk.every
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc

@AutoConfigureRestDocs
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(controllers = [
    ClubController::class]
)
abstract class ApiDocumentationTestV2: BaseTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    // AuthArgumentResolver
    @MockkBean
    lateinit var authorizeArgumentResolver: AuthorizeArgumentResolver

    @MockkBean
    lateinit var userRepository: UserRepository

    @MockkBean
    lateinit var jwtTokenProvider: JwtTokenProvider

    @MockkBean
    lateinit var userDetailsProvider: UserDetailsProvider

    // @AuthUser - ArgumentResolver 모킹
    fun initMockAuthUser(user: User) {
        every { authorizeArgumentResolver.resolveArgument(any(), any(), any(), any()) }.returns(user)
    }
}