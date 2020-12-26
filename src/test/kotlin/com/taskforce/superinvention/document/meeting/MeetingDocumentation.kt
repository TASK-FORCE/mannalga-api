package com.taskforce.superinvention.document.meeting

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.ClubDto
import com.taskforce.superinvention.app.web.dto.club.ClubUserWithUserDto
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingRequestDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingDto
import com.taskforce.superinvention.app.web.dto.role.RoleDto
import com.taskforce.superinvention.app.web.dto.user.UserDto
import com.taskforce.superinvention.common.util.extendFun.DATE_TIME_FORMAT
import com.taskforce.superinvention.common.util.extendFun.toBaseDate
import com.taskforce.superinvention.common.util.extendFun.toBaseDateTime
import com.taskforce.superinvention.config.MockitoHelper
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.test.ApiDocumentationTest
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito.anyLong
import org.mockito.BDDMockito.given
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDate
import java.time.LocalDateTime

class MeetingDocumentation: ApiDocumentationTest() {

    val club = Club("name", "desc", 3L, "sdasd.jpg")
    val clubUser = ClubUser(
            club,
            User("eric"),
            false
    ).apply { this.seq = 123 }

    val currentUser = ClubUser(
            Club(
                    name = "땔감 스터디",
                    description = "땔깜중에서도 고오급 땔깜이 되기 위해 노력하는 스터디",
                    maximumNumber = 5L,
                    mainImageUrl = "s3urlhost/d2e4dxxadf2E.png"
            ),
            User("eric"),
            false
    ).apply { this.seq = 1234L }

    val clubSeq = 76L
    val pageable: Pageable = PageRequest.of(0, 20)

    val clubDto = ClubDto(
            seq = clubSeq,
            name = "club name",
            description = "club description",
            maximumNumber = 100,
            userCount = null,
            mainImageUrl = "asdasd.jpg"
    )

    val clubUserDto = ClubUserWithUserDto(
            seq = 512,
            user = UserDto(
                   4,
                   mutableSetOf(),
                    "eric",
                    LocalDate.now().toBaseDate(),
                    "asfasfsaf.png"

            ),
            club = clubDto,
            roles = setOf(RoleDto(Role.RoleName.CLUB_MEMBER, "CLUB_ROLE"))
    )

    val meetingDto = MeetingDto(
            seq = 1,
            title = "title",
            content = "contents",
            startTimestamp = LocalDateTime.now().toBaseDateTime(),
            endTimestamp = LocalDateTime.now().plusDays(1).toBaseDateTime(),
            club = clubDto,
            deleteFlag = false,
            maximumNumber = 30,
            regClubUser = clubUserDto,
            meetingApplications = arrayListOf(
            ),
            currentClubUserSeq = 512,
            isCurrentUserApplicationMeeting = true
    ).apply {
        this.meetingApplications += this.MeetingApplicationDto(
                seq = 1,
                deleteFlag = false,
                createdAt = LocalDateTime.now().toBaseDateTime(),
                updatedAt = "",
                userSeq = 1,
                regUserFlag = true,
                profileImageLink = "asfafxcv.jpeg",
                userName = "eric is cute"
        )
    }

    val meetingSeq = 1

    val meetingList = listOf(
            meetingDto
    )

    val meetings: Page<MeetingDto> = PageImpl(meetingList, pageable, meetingList.size.toLong())

    val meetingAddRequestDto = MeetingRequestDto(
            title = "modified title",
            content = "modified content",
            startTimestamp = LocalDateTime.parse("2020-08-08T10:00:00"),
            endTimestamp = LocalDateTime.parse("2020-08-09T11:00:00"),
            maximumNumber = 20
    )

    @Test
    @WithMockUser(authorities = [Role.MEMBER])
    fun `만남 조회 기능`() {
        // given
        given(roleService.hasClubMemberAuth(ArgumentMatchers.anyLong(), MockitoHelper.anyObject())).willReturn(true)
        given(meetingService.getMeetings(ArgumentMatchers.anyLong(), MockitoHelper.anyObject(), ArgumentMatchers.anyLong())).willReturn(PageDto(meetings))
        given(clubService.getClubUser(ArgumentMatchers.anyLong(), MockitoHelper.anyObject())).willReturn(currentUser)


        // when
        val result: ResultActions = this.mockMvc.perform(
                get("/clubs/{clubSeq}/meetings", clubSeq)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(document("meeting-all", getDocumentRequest(), getDocumentResponse(),
                        pathParameters(parameterWithName("clubSeq").description("모임 시퀀스")),
                        responseFields(
                                *ApiDocumentUtil.commonResponseField(),
                                *ApiDocumentUtil.pageFieldDescriptor(),

                                fieldWithPath("data.content.[].seq").type(JsonFieldType.NUMBER).description("만남 시퀀스"),
                                fieldWithPath("data.content.[].title").type(JsonFieldType.STRING).description("만남 제목"),
                                fieldWithPath("data.content.[].content").type(JsonFieldType.STRING).description("만남 상세 내용"),
                                fieldWithPath("data.content.[].startTimestamp").type(JsonFieldType.STRING).description("만남 시작 시간. $DATE_TIME_FORMAT 형식으로 전송하면 받을 수 있다."),
                                fieldWithPath("data.content.[].endTimestamp").type(JsonFieldType.STRING).description("만남 종료 시간. $DATE_TIME_FORMAT 형식으로 전송하면 받을 수 있다."),
                                fieldWithPath("data.content.[].club").type(JsonFieldType.OBJECT).description("만남을 진행하는 모임 정보"),
                                fieldWithPath("data.content.[].club.seq").type(JsonFieldType.NUMBER).description("모임 시퀀스"),
                                fieldWithPath("data.content.[].club.name").type(JsonFieldType.STRING).description("모임 모임명"),
                                fieldWithPath("data.content.[].club.description").type(JsonFieldType.STRING).description("모임 상세설명"),
                                fieldWithPath("data.content.[].club.maximumNumber").type(JsonFieldType.NUMBER).description("모임 모임 최대 인원"),
                                fieldWithPath("data.content.[].club.mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지"),
                                fieldWithPath("data.content.[].club.userCount").type(JsonFieldType.NULL).description("사용하지 않는 필드"),
                                fieldWithPath("data.content.[].deleteFlag").type(JsonFieldType.BOOLEAN).description("만남 삭제 여부"),
                                fieldWithPath("data.content.[].maximumNumber").type(JsonFieldType.NUMBER).description("만남 최대 제한 인원"),
                                fieldWithPath("data.content.[].regClubUser").type(JsonFieldType.OBJECT).description("만남 생성한 모임원 정보"),
                                fieldWithPath("data.content.[].regClubUser.seq").type(JsonFieldType.NUMBER).description("모임원 시퀀스"),
                                fieldWithPath("data.content.[].regClubUser.user").type(JsonFieldType.OBJECT).description("유저 정보"),
                                fieldWithPath("data.content.[].regClubUser.user.seq").type(JsonFieldType.NUMBER).description("회원 시퀀스"),
                                fieldWithPath("data.content.[].regClubUser.user.userRoles").type(JsonFieldType.ARRAY).description("회원 권한"),
                                fieldWithPath("data.content.[].regClubUser.user.userName").type(JsonFieldType.STRING).description("회원명"),
                                fieldWithPath("data.content.[].regClubUser.user.birthday").type(JsonFieldType.STRING).description("생일"),
                                fieldWithPath("data.content.[].regClubUser.user.profileImageLink").type(JsonFieldType.STRING).description("프로필 이미지 링크"),
                                fieldWithPath("data.content.[].regClubUser.club").type(JsonFieldType.OBJECT).description("모임원의 모임 정보"),
                                fieldWithPath("data.content.[].regClubUser.club.seq").type(JsonFieldType.NUMBER).description("모임 시퀀스"),
                                fieldWithPath("data.content.[].regClubUser.club.name").type(JsonFieldType.STRING).description("모임 모임명"),
                                fieldWithPath("data.content.[].regClubUser.club.description").type(JsonFieldType.STRING).description("모임 상세설명"),
                                fieldWithPath("data.content.[].regClubUser.club.maximumNumber").type(JsonFieldType.NUMBER).description("모임 모임 최대 인원"),
                                fieldWithPath("data.content.[].regClubUser.club.mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지"),
                                fieldWithPath("data.content.[].regClubUser.club.userCount").type(JsonFieldType.NULL).description("사용하지 않는 필드"),
                                fieldWithPath("data.content.[].regClubUser.roles").type(JsonFieldType.ARRAY).description("모임원의 권한 정보"),
                                fieldWithPath("data.content.[].regClubUser.roles[].name").type(JsonFieldType.STRING).description("권한 명"),
                                fieldWithPath("data.content.[].regClubUser.roles.[].roleGroupName").type(JsonFieldType.STRING).description("권한그룹 명"),
                                fieldWithPath("data.content.[].meetingApplications").type(JsonFieldType.ARRAY).description("만남을 신청한 모임원들 정보"),
                                fieldWithPath("data.content.[].meetingApplications.[].seq").type(JsonFieldType.NUMBER).description("모임원 시퀀스"),
                                fieldWithPath("data.content.[].meetingApplications.[].deleteFlag").type(JsonFieldType.BOOLEAN).description("만남 신청을 삭제했는지 여부"),
                                fieldWithPath("data.content.[].meetingApplications.[].createdAt").type(JsonFieldType.STRING).description("최초로 만남신청을 진행한 시각"),
                                fieldWithPath("data.content.[].meetingApplications.[].updatedAt").type(JsonFieldType.STRING).description("만남 신청정보를 업데이트한 마지막 시각"),
                                fieldWithPath("data.content.[].meetingApplications.[].userInfo").type(JsonFieldType.OBJECT).description("만남 신청을 진행한 유저의 상세 정보"),
                                fieldWithPath("data.content.[].meetingApplications.[].userInfo.userSeq").type(JsonFieldType.NUMBER).description("유저 정보"),
                                fieldWithPath("data.content.[].meetingApplications.[].userInfo.userName").type(JsonFieldType.STRING).description("유저 이름"),
                                fieldWithPath("data.content.[].meetingApplications.[].userInfo.profileImageLink").type(JsonFieldType.STRING).description("유저의 프로필 이미지 링크"),
                                fieldWithPath("data.content.[].meetingApplications.[].userInfo.regUserFlag").type(JsonFieldType.BOOLEAN).description("해당 유저가 만남을 생성한 유저인지 여부"),
                                fieldWithPath("data.content.[].isCurrentUserRegMeeting").type(JsonFieldType.BOOLEAN).description("현재 접속한 유저가 해당 만남을 생성한 유저인지 여부"),
                                fieldWithPath("data.content.[].isCurrentUserApplicationMeeting").type(JsonFieldType.BOOLEAN).description("현재 접속한 유저가 해당 만남에 만남 신청을 했는지 여부")
                        )
                ))
    }

    @Test
    @WithMockUser(authorities = [Role.MEMBER])
    fun `만남 생성`() {
        // given
        given(clubService.getClubUser(ArgumentMatchers.anyLong(), MockitoHelper.anyObject())).willReturn(clubUser)
        given(roleService.hasClubManagerAuth(MockitoHelper.anyObject())).willReturn(true)
        given(meetingService.createMeeting(MockitoHelper.anyObject(), ArgumentMatchers.anyLong())).willReturn(meetingDto)

        // when
        val result: ResultActions = this.mockMvc.perform(
                post("/clubs/{clubSeq}/meetings", clubSeq)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetingAddRequestDto))
                        .characterEncoding("UTF-8")
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(document("create-meeting", getDocumentRequest(), getDocumentResponse(),
                        pathParameters(parameterWithName("clubSeq").description("모임 시퀀스")),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("만남 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("만남 내용"),
                                fieldWithPath("startTimestamp").type(JsonFieldType.STRING).description("만남 시작 시간"),
                                fieldWithPath("endTimestamp").type(JsonFieldType.STRING).description("만남 종료 시간"),
                                fieldWithPath("maximumNumber").type(JsonFieldType.NUMBER).description("만남 최대 인원")
                        ),
                        responseFields(
                                *ApiDocumentUtil.commonResponseField(),

                                fieldWithPath("data.seq").type(JsonFieldType.NUMBER).description("만남 시퀀스"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("만남 제목"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("만남 상세 내용"),
                                fieldWithPath("data.startTimestamp").type(JsonFieldType.STRING).description("만남 시작 시간. $DATE_TIME_FORMAT 형식으로 전송하면 받을 수 있다."),
                                fieldWithPath("data.endTimestamp").type(JsonFieldType.STRING).description("만남 종료 시간. $DATE_TIME_FORMAT 형식으로 전송하면 받을 수 있다."),
                                fieldWithPath("data.club").type(JsonFieldType.OBJECT).description("만남을 진행하는 모임 정보"),
                                fieldWithPath("data.club.seq").type(JsonFieldType.NUMBER).description("모임 시퀀스"),
                                fieldWithPath("data.club.name").type(JsonFieldType.STRING).description("모임 모임명"),
                                fieldWithPath("data.club.description").type(JsonFieldType.STRING).description("모임 상세설명"),
                                fieldWithPath("data.club.maximumNumber").type(JsonFieldType.NUMBER).description("모임 모임 최대 인원"),
                                fieldWithPath("data.club.mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지"),
                                fieldWithPath("data.club.userCount").type(JsonFieldType.NULL).description("사용하지 않는 필드"),
                                fieldWithPath("data.deleteFlag").type(JsonFieldType.BOOLEAN).description("만남 삭제 여부"),
                                fieldWithPath("data.maximumNumber").type(JsonFieldType.NUMBER).description("만남 최대 제한 인원"),
                                fieldWithPath("data.regClubUser").type(JsonFieldType.OBJECT).description("만남 생성한 모임원 정보"),
                                fieldWithPath("data.regClubUser.seq").type(JsonFieldType.NUMBER).description("모임원 시퀀스"),
                                fieldWithPath("data.regClubUser.user").type(JsonFieldType.OBJECT).description("유저 정보"),
                                fieldWithPath("data.regClubUser.user.seq").type(JsonFieldType.NUMBER).description("회원 시퀀스"),
                                fieldWithPath("data.regClubUser.user.userRoles").type(JsonFieldType.ARRAY).description("회원 권한"),
                                fieldWithPath("data.regClubUser.user.userName").type(JsonFieldType.STRING).description("회원명"),
                                fieldWithPath("data.regClubUser.user.birthday").type(JsonFieldType.STRING).description("생일"),
                                fieldWithPath("data.regClubUser.user.profileImageLink").type(JsonFieldType.STRING).description("프로필 이미지 링크"),
                                fieldWithPath("data.regClubUser.club").type(JsonFieldType.OBJECT).description("모임원의 모임 정보"),
                                fieldWithPath("data.regClubUser.club.seq").type(JsonFieldType.NUMBER).description("모임 시퀀스"),
                                fieldWithPath("data.regClubUser.club.name").type(JsonFieldType.STRING).description("모임 모임명"),
                                fieldWithPath("data.regClubUser.club.description").type(JsonFieldType.STRING).description("모임 상세설명"),
                                fieldWithPath("data.regClubUser.club.maximumNumber").type(JsonFieldType.NUMBER).description("모임 모임 최대 인원"),
                                fieldWithPath("data.regClubUser.club.mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지"),
                                fieldWithPath("data.regClubUser.club.userCount").type(JsonFieldType.NULL).description("사용하지 않는 필드"),
                                fieldWithPath("data.regClubUser.roles").type(JsonFieldType.ARRAY).description("모임원의 권한 정보"),
                                fieldWithPath("data.regClubUser.roles[].name").type(JsonFieldType.STRING).description("권한 명"),
                                fieldWithPath("data.regClubUser.roles.[].roleGroupName").type(JsonFieldType.STRING).description("권한그룹 명"),
                                fieldWithPath("data.meetingApplications").type(JsonFieldType.ARRAY).description("만남을 신청한 모임원들 정보"),
                                fieldWithPath("data.meetingApplications.[].seq").type(JsonFieldType.NUMBER).description("모임원 시퀀스"),
                                fieldWithPath("data.meetingApplications.[].deleteFlag").type(JsonFieldType.BOOLEAN).description("만남 신청을 삭제했는지 여부"),
                                fieldWithPath("data.meetingApplications.[].createdAt").type(JsonFieldType.STRING).description("최초로 만남신청을 진행한 시각"),
                                fieldWithPath("data.meetingApplications.[].updatedAt").type(JsonFieldType.STRING).description("만남 신청정보를 업데이트한 마지막 시각"),
                                fieldWithPath("data.meetingApplications.[].userInfo").type(JsonFieldType.OBJECT).description("만남 신청을 진행한 유저의 상세 정보"),
                                fieldWithPath("data.meetingApplications.[].userInfo.userSeq").type(JsonFieldType.NUMBER).description("유저 정보"),
                                fieldWithPath("data.meetingApplications.[].userInfo.userName").type(JsonFieldType.STRING).description("유저 이름"),
                                fieldWithPath("data.meetingApplications.[].userInfo.profileImageLink").type(JsonFieldType.STRING).description("유저의 프로필 이미지 링크"),
                                fieldWithPath("data.meetingApplications.[].userInfo.regUserFlag").type(JsonFieldType.BOOLEAN).description("해당 유저가 만남을 생성한 유저인지 여부"),
                                fieldWithPath("data.isCurrentUserRegMeeting").type(JsonFieldType.BOOLEAN).description("현재 접속한 유저가 해당 만남을 생성한 유저인지 여부"),
                                fieldWithPath("data.isCurrentUserApplicationMeeting").type(JsonFieldType.BOOLEAN).description("현재 접속한 유저가 해당 만남에 만남 신청을 했는지 여부")

                        )
                ))


    }

    @Test
    @WithMockUser(authorities = [Role.MEMBER])
    fun `만남 수정`() {




        given(clubService.getClubUser(ArgumentMatchers.anyLong(), MockitoHelper.anyObject())).willReturn(clubUser)
        given(roleService.hasClubManagerAuth(MockitoHelper.anyObject())).willReturn(true)
        given(meetingService.modifyMeeting(ArgumentMatchers.anyLong(), MockitoHelper.anyObject(), MockitoHelper.anyObject())).willReturn(meetingDto)

        // when
        val result: ResultActions = this.mockMvc.perform(
                put("/clubs/{clubSeq}/meetings/{meetingSeq}", clubSeq, meetingSeq)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(meetingAddRequestDto))
                        .characterEncoding("UTF-8")
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(document("modify-meeting", getDocumentRequest(), getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스"),
                                parameterWithName("meetingSeq").description("만남 시퀀스")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("변경할 만남 제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("변경할 만남 내용"),
                                fieldWithPath("startTimestamp").type(JsonFieldType.STRING).description("변경할 만남 시작 시간"),
                                fieldWithPath("endTimestamp").type(JsonFieldType.STRING).description("변경할 만남 종료 시간"),
                                fieldWithPath("maximumNumber").type(JsonFieldType.NUMBER).description("변경할 만남 최대 인원")
                        ),
                        responseFields(
                                *ApiDocumentUtil.commonResponseField(),
                                fieldWithPath("data.seq").type(JsonFieldType.NUMBER).description("만남 시퀀스"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("만남 제목"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("만남 상세 내용"),
                                fieldWithPath("data.startTimestamp").type(JsonFieldType.STRING).description("만남 시작 시간. $DATE_TIME_FORMAT 형식으로 전송하면 받을 수 있다."),
                                fieldWithPath("data.endTimestamp").type(JsonFieldType.STRING).description("만남 종료 시간. $DATE_TIME_FORMAT 형식으로 전송하면 받을 수 있다."),
                                fieldWithPath("data.club").type(JsonFieldType.OBJECT).description("만남을 진행하는 모임 정보"),
                                fieldWithPath("data.club.seq").type(JsonFieldType.NUMBER).description("모임 시퀀스"),
                                fieldWithPath("data.club.name").type(JsonFieldType.STRING).description("모임 모임명"),
                                fieldWithPath("data.club.description").type(JsonFieldType.STRING).description("모임 상세설명"),
                                fieldWithPath("data.club.maximumNumber").type(JsonFieldType.NUMBER).description("모임 모임 최대 인원"),
                                fieldWithPath("data.club.mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지"),
                                fieldWithPath("data.club.userCount").type(JsonFieldType.NULL).description("사용하지 않는 필드"),
                                fieldWithPath("data.deleteFlag").type(JsonFieldType.BOOLEAN).description("만남 삭제 여부"),
                                fieldWithPath("data.maximumNumber").type(JsonFieldType.NUMBER).description("만남 최대 제한 인원"),
                                fieldWithPath("data.regClubUser").type(JsonFieldType.OBJECT).description("만남 생성한 모임원 정보"),
                                fieldWithPath("data.regClubUser.seq").type(JsonFieldType.NUMBER).description("모임원 시퀀스"),
                                fieldWithPath("data.regClubUser.user").type(JsonFieldType.OBJECT).description("유저 정보"),
                                fieldWithPath("data.regClubUser.user.seq").type(JsonFieldType.NUMBER).description("회원 시퀀스"),
                                fieldWithPath("data.regClubUser.user.userRoles").type(JsonFieldType.ARRAY).description("회원 권한"),
                                fieldWithPath("data.regClubUser.user.userName").type(JsonFieldType.STRING).description("회원명"),
                                fieldWithPath("data.regClubUser.user.birthday").type(JsonFieldType.STRING).description("생일"),
                                fieldWithPath("data.regClubUser.user.profileImageLink").type(JsonFieldType.STRING).description("프로필 이미지 링크"),
                                fieldWithPath("data.regClubUser.club").type(JsonFieldType.OBJECT).description("모임원의 모임 정보"),
                                fieldWithPath("data.regClubUser.club.seq").type(JsonFieldType.NUMBER).description("모임 시퀀스"),
                                fieldWithPath("data.regClubUser.club.name").type(JsonFieldType.STRING).description("모임 모임명"),
                                fieldWithPath("data.regClubUser.club.description").type(JsonFieldType.STRING).description("모임 상세설명"),
                                fieldWithPath("data.regClubUser.club.maximumNumber").type(JsonFieldType.NUMBER).description("모임 모임 최대 인원"),
                                fieldWithPath("data.regClubUser.club.mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지"),
                                fieldWithPath("data.regClubUser.club.userCount").type(JsonFieldType.NULL).description("사용하지 않는 필드"),
                                fieldWithPath("data.regClubUser.roles").type(JsonFieldType.ARRAY).description("모임원의 권한 정보"),
                                fieldWithPath("data.regClubUser.roles[].name").type(JsonFieldType.STRING).description("권한 명"),
                                fieldWithPath("data.regClubUser.roles.[].roleGroupName").type(JsonFieldType.STRING).description("권한그룹 명"),
                                fieldWithPath("data.meetingApplications").type(JsonFieldType.ARRAY).description("만남을 신청한 모임원들 정보"),
                                fieldWithPath("data.meetingApplications.[].seq").type(JsonFieldType.NUMBER).description("모임원 시퀀스"),
                                fieldWithPath("data.meetingApplications.[].deleteFlag").type(JsonFieldType.BOOLEAN).description("만남 신청을 삭제했는지 여부"),
                                fieldWithPath("data.meetingApplications.[].createdAt").type(JsonFieldType.STRING).description("최초로 만남신청을 진행한 시각"),
                                fieldWithPath("data.meetingApplications.[].updatedAt").type(JsonFieldType.STRING).description("만남 신청정보를 업데이트한 마지막 시각"),
                                fieldWithPath("data.meetingApplications.[].userInfo").type(JsonFieldType.OBJECT).description("만남 신청을 진행한 유저의 상세 정보"),
                                fieldWithPath("data.meetingApplications.[].userInfo.userSeq").type(JsonFieldType.NUMBER).description("유저 정보"),
                                fieldWithPath("data.meetingApplications.[].userInfo.userName").type(JsonFieldType.STRING).description("유저 이름"),
                                fieldWithPath("data.meetingApplications.[].userInfo.profileImageLink").type(JsonFieldType.STRING).description("유저의 프로필 이미지 링크"),
                                fieldWithPath("data.meetingApplications.[].userInfo.regUserFlag").type(JsonFieldType.BOOLEAN).description("해당 유저가 만남을 생성한 유저인지 여부"),
                                fieldWithPath("data.isCurrentUserRegMeeting").type(JsonFieldType.BOOLEAN).description("현재 접속한 유저가 해당 만남을 생성한 유저인지 여부"),
                                fieldWithPath("data.isCurrentUserApplicationMeeting").type(JsonFieldType.BOOLEAN).description("현재 접속한 유저가 해당 만남에 만남 신청을 했는지 여부")

                        )
                ))
    }


    @Test
    @WithMockUser(authorities = [Role.MEMBER])
    fun `만남 삭제`() {
        // given
        given(clubService.getClubUser(ArgumentMatchers.anyLong(), MockitoHelper.anyObject())).willReturn(clubUser)
        given(roleService.hasClubManagerAuth(MockitoHelper.anyObject())).willReturn(true)

        // when
        val result: ResultActions = this.mockMvc.perform(
                delete("/clubs/{clubSeq}/meetings/{meetingSeq}", clubSeq, meetingSeq)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(
                document(
            "delete-meeting", getDocumentRequest(), getDocumentResponse(),
                    pathParameters(
                        parameterWithName("clubSeq").description("모임 시퀀스"),
                        parameterWithName("meetingSeq").description("만남 시퀀스")
                    ),
                    responseFields(
                        *ApiDocumentUtil.commonResponseField()
                    )
                )
            )
    }

    @Test
    @WithMockUser(authorities = [Role.MEMBER])
    fun `만남 개별건 조회`() {
        // given
        given(clubService.getClubUser(ArgumentMatchers.anyLong(), MockitoHelper.anyObject())).willReturn(clubUser)
        given(meetingService.getMeeting(anyLong(), anyLong())).willReturn(meetingDto)

        // when
        val result: ResultActions = this.mockMvc.perform(
                get("/clubs/{clubSeq}/meetings/{meetingSeq}", clubSeq, meetingSeq)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(document("meeting-one", getDocumentRequest(), getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스"),
                                parameterWithName("meetingSeq").description("만남 시퀀스")
                        ),
                        responseFields(
                                *ApiDocumentUtil.commonResponseField(),

                                fieldWithPath("data.seq").type(JsonFieldType.NUMBER).description("만남 시퀀스"),
                                fieldWithPath("data.title").type(JsonFieldType.STRING).description("만남 제목"),
                                fieldWithPath("data.content").type(JsonFieldType.STRING).description("만남 상세 내용"),
                                fieldWithPath("data.startTimestamp").type(JsonFieldType.STRING).description("만남 시작 시간. $DATE_TIME_FORMAT 형식으로 전송하면 받을 수 있다."),
                                fieldWithPath("data.endTimestamp").type(JsonFieldType.STRING).description("만남 종료 시간. $DATE_TIME_FORMAT 형식으로 전송하면 받을 수 있다."),
                                fieldWithPath("data.club").type(JsonFieldType.OBJECT).description("만남을 진행하는 모임 정보"),
                                fieldWithPath("data.club.seq").type(JsonFieldType.NUMBER).description("모임 시퀀스"),
                                fieldWithPath("data.club.name").type(JsonFieldType.STRING).description("모임 모임명"),
                                fieldWithPath("data.club.description").type(JsonFieldType.STRING).description("모임 상세설명"),
                                fieldWithPath("data.club.maximumNumber").type(JsonFieldType.NUMBER).description("모임 모임 최대 인원"),
                                fieldWithPath("data.club.mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지"),
                                fieldWithPath("data.club.userCount").type(JsonFieldType.NULL).description("사용하지 않는 필드"),
                                fieldWithPath("data.deleteFlag").type(JsonFieldType.BOOLEAN).description("만남 삭제 여부"),
                                fieldWithPath("data.maximumNumber").type(JsonFieldType.NUMBER).description("만남 최대 제한 인원"),
                                fieldWithPath("data.regClubUser").type(JsonFieldType.OBJECT).description("만남 생성한 모임원 정보"),
                                fieldWithPath("data.regClubUser.seq").type(JsonFieldType.NUMBER).description("모임원 시퀀스"),
                                fieldWithPath("data.regClubUser.user").type(JsonFieldType.OBJECT).description("유저 정보"),
                                fieldWithPath("data.regClubUser.user.seq").type(JsonFieldType.NUMBER).description("회원 시퀀스"),
                                fieldWithPath("data.regClubUser.user.userRoles").type(JsonFieldType.ARRAY).description("회원 권한"),
                                fieldWithPath("data.regClubUser.user.userName").type(JsonFieldType.STRING).description("회원명"),
                                fieldWithPath("data.regClubUser.user.birthday").type(JsonFieldType.STRING).description("생일"),
                                fieldWithPath("data.regClubUser.user.profileImageLink").type(JsonFieldType.STRING).description("프로필 이미지 링크"),
                                fieldWithPath("data.regClubUser.club").type(JsonFieldType.OBJECT).description("모임원의 모임 정보"),
                                fieldWithPath("data.regClubUser.club.seq").type(JsonFieldType.NUMBER).description("모임 시퀀스"),
                                fieldWithPath("data.regClubUser.club.name").type(JsonFieldType.STRING).description("모임 모임명"),
                                fieldWithPath("data.regClubUser.club.description").type(JsonFieldType.STRING).description("모임 상세설명"),
                                fieldWithPath("data.regClubUser.club.maximumNumber").type(JsonFieldType.NUMBER).description("모임 모임 최대 인원"),
                                fieldWithPath("data.regClubUser.club.mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지"),
                                fieldWithPath("data.regClubUser.club.userCount").type(JsonFieldType.NULL).description("사용하지 않는 필드"),
                                fieldWithPath("data.regClubUser.roles").type(JsonFieldType.ARRAY).description("모임원의 권한 정보"),
                                fieldWithPath("data.regClubUser.roles[].name").type(JsonFieldType.STRING).description("권한 명"),
                                fieldWithPath("data.regClubUser.roles.[].roleGroupName").type(JsonFieldType.STRING).description("권한그룹 명"),
                                fieldWithPath("data.meetingApplications").type(JsonFieldType.ARRAY).description("만남을 신청한 모임원들 정보"),
                                fieldWithPath("data.meetingApplications.[].seq").type(JsonFieldType.NUMBER).description("모임원 시퀀스"),
                                fieldWithPath("data.meetingApplications.[].deleteFlag").type(JsonFieldType.BOOLEAN).description("만남 신청을 삭제했는지 여부"),
                                fieldWithPath("data.meetingApplications.[].createdAt").type(JsonFieldType.STRING).description("최초로 만남신청을 진행한 시각"),
                                fieldWithPath("data.meetingApplications.[].updatedAt").type(JsonFieldType.STRING).description("만남 신청정보를 업데이트한 마지막 시각"),
                                fieldWithPath("data.meetingApplications.[].userInfo").type(JsonFieldType.OBJECT).description("만남 신청을 진행한 유저의 상세 정보"),
                                fieldWithPath("data.meetingApplications.[].userInfo.userSeq").type(JsonFieldType.NUMBER).description("유저 정보"),
                                fieldWithPath("data.meetingApplications.[].userInfo.userName").type(JsonFieldType.STRING).description("유저 이름"),
                                fieldWithPath("data.meetingApplications.[].userInfo.profileImageLink").type(JsonFieldType.STRING).description("유저의 프로필 이미지 링크"),
                                fieldWithPath("data.meetingApplications.[].userInfo.regUserFlag").type(JsonFieldType.BOOLEAN).description("해당 유저가 만남을 생성한 유저인지 여부"),
                                fieldWithPath("data.isCurrentUserRegMeeting").type(JsonFieldType.BOOLEAN).description("현재 접속한 유저가 해당 만남을 생성한 유저인지 여부"),
                                fieldWithPath("data.isCurrentUserApplicationMeeting").type(JsonFieldType.BOOLEAN).description("현재 접속한 유저가 해당 만남에 만남 신청을 했는지 여부")

                        )
                    )
                )
    }


}