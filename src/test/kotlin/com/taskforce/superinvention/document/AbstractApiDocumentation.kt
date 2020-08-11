package com.taskforce.superinvention.document

import com.fasterxml.jackson.databind.ObjectMapper
import com.taskforce.superinvention.app.domain.state.StateController
import com.taskforce.superinvention.app.domain.user.UserController
import com.taskforce.superinvention.app.domain.user.UserDetailsService
import com.taskforce.superinvention.app.domain.user.UserRepository
import com.taskforce.superinvention.app.domain.user.UserRepositorySupport
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc

@ExtendWith(SpringExtension::class)
@WebMvcTest(controllers = [ StateController::class ] )
@Import(JwtTokenProvider::class, UserDetailsService::class)
@AutoConfigureRestDocs
abstract class AbstractApiDocumentation {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var userRepository: UserRepository
}