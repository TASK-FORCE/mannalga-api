package com.taskforce.superinvention.document.meeting

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.meeting.Meeting
import com.taskforce.superinvention.app.domain.meeting.MeetingApplication
import com.taskforce.superinvention.app.domain.role.ClubUserRole
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.role.RoleGroup
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.meeting.MeetingApplicationDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingApplicationStatusDto
import com.taskforce.superinvention.common.util.extendFun.DATE_TIME_FORMAT
import com.taskforce.superinvention.config.MockitoHelper
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil
import com.taskforce.superinvention.config.test.ApiDocumentationTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.time.LocalDate
import java.time.LocalDateTime

class MeetingApplicationDocumentation: ApiDocumentationTest() {

    lateinit var club: Club
    lateinit var user: User
    lateinit var clubUser : ClubUser
    lateinit var meeting: Meeting
    lateinit var meetingApplication: MeetingApplication



    @BeforeEach
    fun setup() {
        club = Club(
                name = "테스트 모임",
                description   = "",
                maximumNumber = 10,
                mainImageUrl  = ""
        ).apply { seq = 88 }

        user = User ("1").apply {
            seq = 2
            userName = "eric"
            birthday = LocalDate.now()
            profileImageLink = "asgaSGASGA.png"
        }

        clubUser = ClubUser(club, user, isLiked = true).apply { seq  = 110 }

        clubUser.clubUserRoles = mutableSetOf(
                ClubUserRole(clubUser, Role(Role.RoleName.CLUB_MEMBER, RoleGroup("ROLE_NAME", "ROLE_GROUP_TYPE")))
        )

        meeting = Meeting(
                title = "Test title",
                content = "test content",
                startTimestamp = LocalDateTime.of(2020, 12, 27, 11, 0),
                endTimestamp = LocalDateTime.of(2020, 12, 27, 18, 0),
                club = club,
                regClubUser = clubUser,
                maximumNumber = 20,
                deleteFlag = false,
                region = "천호역 4번출구",
                regionURL = "map.kakao.com/asdas",
                cost = 12450
        ).apply { seq = 1243 }

        meetingApplication = MeetingApplication(clubUser, meeting, false).apply { seq = 1525 }
        meeting.meetingApplications = listOf(meetingApplication)

    }


    @Test
    fun `만남 신청`() {
        // given
        `when`(clubService.getClubUser(anyLong(), MockitoHelper.anyObject())).thenReturn(clubUser)
        `when`(meetingService.application(MockitoHelper.anyObject(), anyLong())).thenReturn(MeetingApplicationDto(meetingApplication))

        // when
        val result: ResultActions = this.mockMvc.perform(
                post("/clubs/{clubSeq}/meetings/{meetingSeq}/applications", club.seq, meeting.seq)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(MockMvcRestDocumentation.document("meeting-application", ApiDocumentUtil.getDocumentRequest(), ApiDocumentUtil.getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스"),
                                parameterWithName("meetingSeq").description("만남 시퀀스")
                        ),
                        responseFields(
                                *ApiDocumentUtil.commonResponseField()
                        )
                ))
    }


    @Test
    fun `만남 신청 취소`() {
        // given
        `when`(clubService.getClubUser(anyLong(), MockitoHelper.anyObject())).thenReturn(clubUser)
        `when`(meetingService.applicationCancel(MockitoHelper.anyObject(), anyLong())).thenReturn(MeetingApplicationDto(meetingApplication))
        `when`(meetingService.isRegUser(MockitoHelper.anyObject(), MockitoHelper.anyObject())).thenReturn(true)

        // when
        val result: ResultActions = this.mockMvc.perform(
                delete("/clubs/{clubSeq}/meetings/{meetingSeq}/applications/{meetingApplicationSeq}", club.seq, meeting.seq, meetingApplication.seq)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(MockMvcRestDocumentation.document("meeting-application-cancel", ApiDocumentUtil.getDocumentRequest(), ApiDocumentUtil.getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스"),
                                parameterWithName("meetingSeq").description("만남 시퀀스"),
                                parameterWithName("meetingApplicationSeq").description("만남 신청 시퀀스")
                        ),
                        responseFields(
                                *ApiDocumentUtil.commonResponseField()
                        )
                ))
    }

    @Test
    fun `만남 정보 조회`() {
        // given
        `when`(clubService.getClubUser(anyLong(), MockitoHelper.anyObject())).thenReturn(clubUser)
        `when`(meetingService.getMeetingApplication(anyLong())).thenReturn(MeetingApplicationDto(meetingApplication))
        `when`(meetingService.isRegUser(MockitoHelper.anyObject(), MockitoHelper.anyObject())).thenReturn(true)

        // when
        val result: ResultActions = this.mockMvc.perform(
                get("/clubs/{clubSeq}/meetings/{meetingSeq}/applications/{meetingApplicationSeq}", club.seq, meeting.seq, meetingApplication.seq)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(MockMvcRestDocumentation.document("get-meeting-application", ApiDocumentUtil.getDocumentRequest(), ApiDocumentUtil.getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스"),
                                parameterWithName("meetingSeq").description("만남 시퀀스"),
                                parameterWithName("meetingApplicationSeq").description("만남 신청 시퀀스")
                        ),
                        responseFields(
                                *ApiDocumentUtil.commonResponseField(),
                                fieldWithPath("data.seq").type(JsonFieldType.NUMBER).description("만남 신청 시퀀스"),
                                fieldWithPath("data.clubUser").type(JsonFieldType.OBJECT).description("모임원 정보"),
                                fieldWithPath("data.clubUser.seq").type(JsonFieldType.NUMBER).description("모임원 시퀀스"),
                                fieldWithPath("data.clubUser.user").type(JsonFieldType.OBJECT).description("회원 정보"),
                                fieldWithPath("data.clubUser.user.seq").type(JsonFieldType.NUMBER).description("회원 시퀀스"),
                                fieldWithPath("data.clubUser.user.userRoles").type(JsonFieldType.ARRAY).description("회원 권한"),
                                fieldWithPath("data.clubUser.user.userName").type(JsonFieldType.STRING).description("회원명"),
                                fieldWithPath("data.clubUser.user.birthday").type(JsonFieldType.STRING).description("생일"),
                                fieldWithPath("data.clubUser.user.profileImageLink").type(JsonFieldType.STRING).description("프로필 이미지 링크"),
                                fieldWithPath("data.clubUser.club").type(JsonFieldType.OBJECT).description("모임 정보"),
                                fieldWithPath("data.clubUser.club.seq").type(JsonFieldType.NUMBER).description("모임 시퀀스"),
                                fieldWithPath("data.clubUser.club.name").type(JsonFieldType.STRING).description("모임명"),
                                fieldWithPath("data.clubUser.club.description").type(JsonFieldType.STRING).description("모임 설명"),
                                fieldWithPath("data.clubUser.club.maximumNumber").type(JsonFieldType.NUMBER).description("모임 최대인원"),
                                fieldWithPath("data.clubUser.club.userCount").type(JsonFieldType.NULL).description("모임 현재인원 (Null able)"),
                                fieldWithPath("data.clubUser.club.mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지"),
                                fieldWithPath("data.clubUser.roles").type(JsonFieldType.ARRAY).description("모임원 권한 정보"),
                                fieldWithPath("data.clubUser.roles[].name").type(JsonFieldType.STRING).description("권한 이름"),
                                fieldWithPath("data.clubUser.roles[].roleGroupName").type(JsonFieldType.STRING).description("권한 그룹 이름"),
                                fieldWithPath("data.deleteFlag").type(JsonFieldType.BOOLEAN).description("만남신청 삭제 여부"),
                                fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("생성 일자"),
                                fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("최종 업데이트 일자")
                        )
                ))
    }


    @Test
    fun `만남신청 상태 정보 조회`() {
        // given
        `when`(clubService.getClubUser(anyLong(), MockitoHelper.anyObject())).thenReturn(clubUser)
        `when`(meetingService.getMeetingApplicationStatus(anyLong(), MockitoHelper.anyObject())).thenReturn(
            MeetingApplicationStatusDto(meeting, clubUser.seq)
        )


        // when
        val result: ResultActions = this.mockMvc.perform(
            get("/clubs/{clubSeq}/meetings/{meetingSeq}/applications/status", club.seq, meeting.seq)
                .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcRestDocumentation.document("get-meeting-application", ApiDocumentUtil.getDocumentRequest(), ApiDocumentUtil.getDocumentResponse(),
                pathParameters(
                    parameterWithName("clubSeq").description("모임 시퀀스"),
                    parameterWithName("meetingSeq").description("만남 시퀀스")
                ),
                responseFields(
                    *ApiDocumentUtil.commonResponseField(),
                    fieldWithPath("data.meetingApplications").type(JsonFieldType.ARRAY).description("만남 신청 정보 리스트"),
                    fieldWithPath("data.meetingApplications[].seq").type(JsonFieldType.NUMBER).description("만남 신청 시퀀스"),
                    fieldWithPath("data.meetingApplications[].clubUser").type(JsonFieldType.OBJECT).description("모임원 정보"),
                    fieldWithPath("data.meetingApplications[].clubUser.seq").type(JsonFieldType.NUMBER).description("모임원 시퀀스"),
                    fieldWithPath("data.meetingApplications[].clubUser.user").type(JsonFieldType.OBJECT).description("회원 정보"),
                    fieldWithPath("data.meetingApplications[].clubUser.user.seq").type(JsonFieldType.NUMBER).description("회원 시퀀스"),
                    fieldWithPath("data.meetingApplications[].clubUser.user.userRoles").type(JsonFieldType.ARRAY).description("회원 권한"),
                    fieldWithPath("data.meetingApplications[].clubUser.user.userName").type(JsonFieldType.STRING).description("회원명"),
                    fieldWithPath("data.meetingApplications[].clubUser.user.birthday").type(JsonFieldType.STRING).description("생일"),
                    fieldWithPath("data.meetingApplications[].clubUser.user.profileImageLink").type(JsonFieldType.STRING).description("프로필 이미지 링크"),
                    fieldWithPath("data.meetingApplications[].clubUser.club").type(JsonFieldType.OBJECT).description("모임 정보"),
                    fieldWithPath("data.meetingApplications[].clubUser.club.seq").type(JsonFieldType.NUMBER).description("모임 시퀀스"),
                    fieldWithPath("data.meetingApplications[].clubUser.club.name").type(JsonFieldType.STRING).description("모임명"),
                    fieldWithPath("data.meetingApplications[].clubUser.club.description").type(JsonFieldType.STRING).description("모임 설명"),
                    fieldWithPath("data.meetingApplications[].clubUser.club.maximumNumber").type(JsonFieldType.NUMBER).description("모임 최대인원"),
                    fieldWithPath("data.meetingApplications[].clubUser.club.userCount").type(JsonFieldType.NULL).description("모임 현재인원 (Null able)"),
                    fieldWithPath("data.meetingApplications[].clubUser.club.mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지"),
                    fieldWithPath("data.meetingApplications[].clubUser.roles").type(JsonFieldType.ARRAY).description("모임원 권한 정보"),
                    fieldWithPath("data.meetingApplications[].clubUser.roles[].name").type(JsonFieldType.STRING).description("권한 이름"),
                    fieldWithPath("data.meetingApplications[].clubUser.roles[].roleGroupName").type(JsonFieldType.STRING).description("권한 그룹 이름"),
                    fieldWithPath("data.meetingApplications[].deleteFlag").type(JsonFieldType.BOOLEAN).description("만남신청 삭제 여부"),
                    fieldWithPath("data.meetingApplications[].createdAt").type(JsonFieldType.STRING).description("생성 일자"),
                    fieldWithPath("data.meetingApplications[].updatedAt").type(JsonFieldType.STRING).description("최종 업데이트 일자"),
                    fieldWithPath("data.currentCount").type(JsonFieldType.NUMBER).description("현재 만남신청한 모임원 수"),
                    fieldWithPath("data.maximumNumber").type(JsonFieldType.NUMBER).description("최대 만남신청 가능한 모임원 수"),
                    fieldWithPath("data.isCurrentUserRegMeeting").type(JsonFieldType.BOOLEAN).description("현재 유저가 만남신청을 한 상태인지 여부"),
                    fieldWithPath("data.isCurrentUserApplicationMeeting").type(JsonFieldType.BOOLEAN).description("현재 유저가 만남을 생성한 유저와 일치하는지 여부")
                )
            ))
    }

}