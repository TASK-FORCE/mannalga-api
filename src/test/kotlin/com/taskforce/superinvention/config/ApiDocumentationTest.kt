package com.taskforce.superinvention.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.common.FileService
import com.taskforce.superinvention.app.domain.interest.interest.InterestService
import com.taskforce.superinvention.app.domain.interest.interestGroup.InterestGroupService
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.state.StateService
import com.taskforce.superinvention.app.domain.user.user.UserDetailsProvider
import com.taskforce.superinvention.app.domain.user.user.UserRepository
import com.taskforce.superinvention.app.domain.user.user.UserService
import com.taskforce.superinvention.app.domain.user.userInterest.UserInterestService
import com.taskforce.superinvention.app.domain.user.userState.UserStateService
import com.taskforce.superinvention.app.web.controller.club.ClubController
import com.taskforce.superinvention.app.web.controller.CommonController
import com.taskforce.superinvention.app.web.controller.InterestGroupController
import com.taskforce.superinvention.app.web.controller.StateController
import com.taskforce.superinvention.app.web.controller.UserController
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import com.taskforce.superinvention.common.util.aws.s3.AwsS3Mo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc


@Import(JwtTokenProvider::class, UserDetailsProvider::class)
@AutoConfigureRestDocs
@WebMvcTest(controllers = [
    StateController::class,
    UserController::class,
    InterestGroupController::class,
    ClubController::class,
    CommonController::class
])
abstract class ApiDocumentationTest: BaseTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var userStateService: UserStateService

    @MockBean
    lateinit var stateService: StateService

    @MockBean
    lateinit var interestGroupService: InterestGroupService

    @MockBean
    lateinit var userService: UserService

    @MockBean
    lateinit var interestService: InterestService

    @MockBean
    lateinit var jwtTokenProvider: JwtTokenProvider

    @MockBean
    lateinit var userDetailsProvider: UserDetailsProvider

    @MockBean
    lateinit var clubService: ClubService

    @MockBean
    lateinit var userInterestService: UserInterestService

    @MockBean
    lateinit var roleService: RoleService
  
    @MockBean
    lateinit var fileService: FileService

    @MockBean
    lateinit var awsS3Mo: AwsS3Mo
}