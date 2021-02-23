package com.taskforce.superinvention.config.test

import com.fasterxml.jackson.databind.ObjectMapper
import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.club.album.ClubAlbumService
import com.taskforce.superinvention.app.domain.club.album.comment.ClubAlbumCommentService
import com.taskforce.superinvention.app.domain.club.album.like.ClubAlbumLikeService
import com.taskforce.superinvention.app.domain.club.board.ClubBoardService
import com.taskforce.superinvention.app.domain.club.user.ClubUserService
import com.taskforce.superinvention.app.domain.common.FileService
import com.taskforce.superinvention.app.domain.common.image.ImageService
import com.taskforce.superinvention.app.domain.interest.interest.InterestService
import com.taskforce.superinvention.app.domain.interest.interestGroup.InterestGroupService
import com.taskforce.superinvention.app.domain.meeting.MeetingService
import com.taskforce.superinvention.app.domain.region.RegionService
import com.taskforce.superinvention.app.domain.role.ClubUserRoleRepository
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.UserDetailsProvider
import com.taskforce.superinvention.app.domain.user.UserInfoService
import com.taskforce.superinvention.app.domain.user.UserRepository
import com.taskforce.superinvention.app.domain.user.UserService
import com.taskforce.superinvention.app.domain.user.userInterest.UserInterestService
import com.taskforce.superinvention.app.domain.user.userRegion.UserRegionService
import com.taskforce.superinvention.app.web.controller.region.RegionController
import com.taskforce.superinvention.app.web.controller.CommonController
import com.taskforce.superinvention.app.web.controller.InterestGroupController
import com.taskforce.superinvention.app.web.controller.club.ClubController
import com.taskforce.superinvention.app.web.controller.club.album.ClubAlbumCommentController
import com.taskforce.superinvention.app.web.controller.club.album.ClubAlbumController
import com.taskforce.superinvention.app.web.controller.club.album.ClubAlbumLikeController
import com.taskforce.superinvention.app.web.controller.meeting.MeetingApplicationController
import com.taskforce.superinvention.app.web.controller.meeting.MeetingController
import com.taskforce.superinvention.app.web.controller.user.UserController
import com.taskforce.superinvention.app.web.controller.user.UserRegionController
import com.taskforce.superinvention.app.web.controller.user.UserInterestController
import com.taskforce.superinvention.common.config.security.JwtTokenProvider
import com.taskforce.superinvention.common.util.aws.s3.AwsS3Mo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.servlet.MockMvc

@Deprecated(
        message = "ApiDocumentationTestV2를 쓰세요.",
        replaceWith = ReplaceWith("com.taskforce.superinvention.config.test.ApiDocumentationTestV2")
)
@AutoConfigureRestDocs
@WebMvcTest(controllers = [
    UserController::class,
    UserRegionController::class,
    ClubController::class,
    ClubAlbumController::class,
    ClubAlbumLikeController::class,
    ClubAlbumCommentController::class,
    RegionController::class,
    InterestGroupController::class,
    CommonController::class,
    UserInterestController::class,
    MeetingController::class,
    MeetingApplicationController::class
])
abstract class ApiDocumentationTest: BaseTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @MockBean
    lateinit var userRepository: UserRepository

    @MockBean
    lateinit var userRegionService: UserRegionService

    @MockBean
    lateinit var regionService: RegionService

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
    lateinit var imageService: ImageService

    @MockBean
    lateinit var awsS3Mo: AwsS3Mo

    @MockBean
    lateinit var clubBoardService: ClubBoardService

    @MockBean
    lateinit var userInfoService: UserInfoService

    @MockBean
    lateinit var meetingService: MeetingService

    @MockBean
    lateinit var clubUserService: ClubUserService

    @MockBean
    lateinit var clubAlbumService: ClubAlbumService

    @MockBean
    lateinit var clubAlbumCommentService: ClubAlbumCommentService

    @MockBean
    lateinit var clubAlbumLikeService: ClubAlbumLikeService

    @MockBean
    lateinit var clubUserRoleRepository: ClubUserRoleRepository
}