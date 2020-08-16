package com.taskforce.superinvention.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.taskforce.superinvention.app.web.InterestGroupController
import com.taskforce.superinvention.app.domain.interest.interestGroup.InterestGroupService
import com.taskforce.superinvention.app.web.StateController
import com.taskforce.superinvention.app.domain.state.StateService
import com.taskforce.superinvention.app.domain.user.UserDetailsService
import com.taskforce.superinvention.app.domain.user.UserRepository
import com.taskforce.superinvention.app.domain.user.UserService
import com.taskforce.superinvention.app.web.UserController
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc


@Import(JwtTokenProvider::class, UserDetailsService::class)
@AutoConfigureRestDocs
@WebMvcTest(controllers = [
    StateController::class,
    UserController::class,
    InterestGroupController::class
] )
abstract class ApiDocumentationTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var stateService: StateService

    @MockBean
    lateinit var interestGroupService: InterestGroupService

    @MockBean
    lateinit var userService: UserService
}