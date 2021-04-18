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
import com.taskforce.superinvention.app.web.dto.meeting.MeetingGroupDto
import com.taskforce.superinvention.app.web.dto.role.RoleDto
import com.taskforce.superinvention.app.web.dto.user.UserDto
import com.taskforce.superinvention.common.util.extendFun.*
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
import java.time.YearMonth

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
            seq = 123,
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

    val startTimestamp = LocalDateTime.now()
    val endTimestamp = LocalDateTime.now().plusDays(1)
    val meetingDto = MeetingDto(
        seq = 1,
        title = "title",
        content = "contents",
        startTimestamp = startTimestamp.toBaseDateTime(),
        endTimestamp = endTimestamp.toBaseDateTime(),
        club = clubDto,
        deleteFlag = false,
        maximumNumber = 30,
        regClubUser = clubUserDto,
        meetingApplications = arrayListOf(
        ),
        currentClubUserSeq = 512,
        isCurrentUserApplicationMeeting = true,
        region = "건대 엔젤리너스",
        regionURL = "map.naver.com/aabb",
        cost = 5000,
        isOpen = true,
        meetingDay = startTimestamp.dayOfMonth,
        meetingDayOfWeek = startTimestamp.dayOfWeek.getKorDisplayName(),
        startDate = startTimestamp.toLocalDate().toKorDate(),
        startTime = startTimestamp.toLocalTime().toBaseTime(),
        endDate = endTimestamp.toLocalDate().toKorDate(),
        endTime = endTimestamp.toLocalTime().toBaseTime()
    ).apply {
        this.meetingApplications += MeetingDto.MeetingApplicationDto(
                seq = 1,
                deleteFlag = false,
                createdAt = startTimestamp,
                updatedAt = "",
                userSeq = 1,
                regUserFlag = true,
                profileImageLink = "asfafxcv.jpeg",
                userName = "eric is cute",
                roles = setOf(RoleDto(MockitoHelper.getRoleByRoleName(Role.RoleName.MANAGER, 4)))
        )
    }

    val meetingSeq = 1

    val meetingList = listOf(MeetingGroupDto(YearMonth.parse("2020-08"), listOf(meetingDto)))

    val meetings: Page<MeetingGroupDto> = PageImpl(meetingList, pageable, meetingList.size.toLong())

    val meetingAddRequestDto = MeetingRequestDto(
            title = "modified title",
            content = "modified content",
            startTimestamp = LocalDateTime.parse("2020-08-08T10:00:00"),
            endTimestamp = LocalDateTime.parse("2020-08-09T11:00:00"),
            maximumNumber = 20,
            region = "건대 엔젤리너스",
            regionURL = "map.naver.com/aabb",
            cost = 5000
    )

    @Test
    @WithMockUser(authorities = [Role.MEMBER])
    fun `만남 조회 기능`() {
        // given
        given(roleService.hasClubMemberAuth(ArgumentMatchers.anyLong(), MockitoHelper.anyObject())).willReturn(true)
        given(meetingService.getMeetingsWithGroup(ArgumentMatchers.anyLong(), MockitoHelper.anyObject(), ArgumentMatchers.anyLong())).willReturn(PageDto(meetings))
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
                                fieldWithPath("data.content.[].groupYearMonth").type(JsonFieldType.STRING).description("그룹화한 만남 연/월, 해당 연/월로 만남을 그룹화하여 데이터를 반환한다"),
                                fieldWithPath("data.content.[].meetings.[].seq").type(JsonFieldType.NUMBER).description("만남 시퀀스"),
                                fieldWithPath("data.content.[].meetings.[].title").type(JsonFieldType.STRING).description("만남 제목"),
                                fieldWithPath("data.content.[].meetings.[].content").type(JsonFieldType.STRING).description("만남 상세 내용"),
                                fieldWithPath("data.content.[].meetings.[].startTimestamp").type(JsonFieldType.STRING).description("만남 시작 시간. $DATE_TIME_FORMAT 형식으로 전송하면 받을 수 있다."),
                                fieldWithPath("data.content.[].meetings.[].endTimestamp").type(JsonFieldType.STRING).description("만남 종료 시간. $DATE_TIME_FORMAT 형식으로 전송하면 받을 수 있다."),
                                fieldWithPath("data.content.[].meetings.[].club").type(JsonFieldType.OBJECT).description("만남을 진행하는 모임 정보"),
                                fieldWithPath("data.content.[].meetings.[].club.seq").type(JsonFieldType.NUMBER).description("모임 시퀀스"),
                                fieldWithPath("data.content.[].meetings.[].club.name").type(JsonFieldType.STRING).description("모임 모임명"),
                                fieldWithPath("data.content.[].meetings.[].club.description").type(JsonFieldType.STRING).description("모임 상세설명"),
                                fieldWithPath("data.content.[].meetings.[].club.maximumNumber").type(JsonFieldType.NUMBER).description("모임 모임 최대 인원"),
                                fieldWithPath("data.content.[].meetings.[].club.mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지"),
                                fieldWithPath("data.content.[].meetings.[].club.userCount").type(JsonFieldType.NULL).description("사용하지 않는 필드"),
                                fieldWithPath("data.content.[].meetings.[].deleteFlag").type(JsonFieldType.BOOLEAN).description("만남 삭제 여부"),
                                fieldWithPath("data.content.[].meetings.[].maximumNumber").type(JsonFieldType.NUMBER).description("만남 최대 제한 인원"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser").type(JsonFieldType.OBJECT).description("만남 생성한 모임원 정보"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser.seq").type(JsonFieldType.NUMBER).description("모임원 시퀀스"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser.user").type(JsonFieldType.OBJECT).description("유저 정보"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser.user.seq").type(JsonFieldType.NUMBER).description("회원 시퀀스"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser.user.userRoles").type(JsonFieldType.ARRAY).description("회원 권한"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser.user.userName").type(JsonFieldType.STRING).description("회원명"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser.user.birthday").type(JsonFieldType.STRING).description("생일"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser.user.profileImageLink").type(JsonFieldType.STRING).description("프로필 이미지 링크"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser.club").type(JsonFieldType.OBJECT).description("모임원의 모임 정보"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser.club.seq").type(JsonFieldType.NUMBER).description("모임 시퀀스"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser.club.name").type(JsonFieldType.STRING).description("모임 모임명"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser.club.description").type(JsonFieldType.STRING).description("모임 상세설명"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser.club.maximumNumber").type(JsonFieldType.NUMBER).description("모임 모임 최대 인원"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser.club.mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser.club.userCount").type(JsonFieldType.NULL).description("사용하지 않는 필드"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser.roles").type(JsonFieldType.ARRAY).description("모임원의 권한 정보"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser.roles[].name").type(JsonFieldType.STRING).description("권한 명"),
                                fieldWithPath("data.content.[].meetings.[].regClubUser.roles.[].roleGroupName").type(JsonFieldType.STRING).description("권한그룹 명"),
                                fieldWithPath("data.content.[].meetings.[].meetingApplications").type(JsonFieldType.ARRAY).description("만남을 신청한 모임원들 정보"),
                                fieldWithPath("data.content.[].meetings.[].meetingApplications.[].seq").type(JsonFieldType.NUMBER).description("모임원 시퀀스"),
                                fieldWithPath("data.content.[].meetings.[].meetingApplications.[].deleteFlag").type(JsonFieldType.BOOLEAN).description("만남 신청을 삭제했는지 여부"),
                                fieldWithPath("data.content.[].meetings.[].meetingApplications.[].createdAt").type(JsonFieldType.STRING).description("최초로 만남신청을 진행한 시각"),
                                fieldWithPath("data.content.[].meetings.[].meetingApplications.[].updatedAt").type(JsonFieldType.STRING).description("만남 신청정보를 업데이트한 마지막 시각"),
                                fieldWithPath("data.content.[].meetings.[].meetingApplications.[].userInfo").type(JsonFieldType.OBJECT).description("만남 신청을 진행한 유저의 상세 정보"),
                                fieldWithPath("data.content.[].meetings.[].meetingApplications.[].userInfo.userSeq").type(JsonFieldType.NUMBER).description("유저 정보"),
                                fieldWithPath("data.content.[].meetings.[].meetingApplications.[].userInfo.userName").type(JsonFieldType.STRING).description("유저 이름"),
                                fieldWithPath("data.content.[].meetings.[].meetingApplications.[].userInfo.profileImageLink").type(JsonFieldType.STRING).description("유저의 프로필 이미지 링크"),
                                fieldWithPath("data.content.[].meetings.[].meetingApplications.[].userInfo.regUserFlag").type(JsonFieldType.BOOLEAN).description("해당 유저가 만남을 생성한 유저인지 여부"),
                                fieldWithPath("data.content.[].meetings.[].meetingApplications.[].roles.[]").type(JsonFieldType.ARRAY).description("신청자의 모임 권한"),
                                fieldWithPath("data.content.[].meetings.[].meetingApplications.[].roles.[].name").type(JsonFieldType.STRING).description("권한 명"),
                                fieldWithPath("data.content.[].meetings.[].meetingApplications.[].roles.[].roleGroupName").type(JsonFieldType.STRING).description("권한그룹 명"),
                                fieldWithPath("data.content.[].meetings.[].isCurrentUserRegMeeting").type(JsonFieldType.BOOLEAN).description("현재 접속한 유저가 해당 만남을 생성한 유저인지 여부"),
                                fieldWithPath("data.content.[].meetings.[].isCurrentUserApplicationMeeting").type(JsonFieldType.BOOLEAN).description("현재 접속한 유저가 해당 만남에 만남 신청을 했는지 여부"),
                                fieldWithPath("data.content.[].meetings.[].region").type(JsonFieldType.STRING).description("만남을 어느 지역에서 하는지 여부 (nullable)"),
                                fieldWithPath("data.content.[].meetings.[].regionURL").type(JsonFieldType.STRING).description("만남을 지역과 관련된 URL, 지도 URL등을 입력 가능 (nullable)"),
                                fieldWithPath("data.content.[].meetings.[].cost").type(JsonFieldType.NUMBER).description("만남 진행 시 필요한 금액(nullable)") ,
                                fieldWithPath("data.content.[].meetings.[].isOpen").type(JsonFieldType.BOOLEAN).description("만남이 현재 활성상태인지 검사한다. 종료 시간을 기준으로 현재보다 만남 종료시간이 이후라면 활성상태로 판단한다."),
                                fieldWithPath("data.content.[].meetings.[].meetingDay").type(JsonFieldType.NUMBER).description("만남을 시작하는 날짜") ,
                                fieldWithPath("data.content.[].meetings.[].meetingDayOfWeek").type(JsonFieldType.STRING).description("만남을 시작하는 요일") ,
                                fieldWithPath("data.content.[].meetings.[].startDate").type(JsonFieldType.STRING).description("만남을 시작하는 날짜, $DATE_FORMAT_KOR") ,
                                fieldWithPath("data.content.[].meetings.[].startTime").type(JsonFieldType.STRING).description("만남을 시작하는 시간, $TIME_FORMAT") ,
                                fieldWithPath("data.content.[].meetings.[].endDate").type(JsonFieldType.STRING).description("만남을 종료하는 날짜, $DATE_FORMAT_KOR") ,
                                fieldWithPath("data.content.[].meetings.[].endTime").type(JsonFieldType.STRING).description("만남을 종료하는 시간, $TIME_FORMAT") ,


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
                                fieldWithPath("maximumNumber").type(JsonFieldType.NUMBER).description("만남 최대 인원"),
                                fieldWithPath("region").type(JsonFieldType.STRING).description("만남 진행 장소 (nullable)"),
                                fieldWithPath("regionURL").type(JsonFieldType.STRING).description("만남을 지역과 관련된 URL, 지도 URL등을 입력 가능 (nullable)"),
                                fieldWithPath("cost").type(JsonFieldType.NUMBER).description("만남진행 시 필요한 금액(nullable)")
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
                                fieldWithPath("data.meetingApplications.[].roles.[]").type(JsonFieldType.ARRAY).description("신청자의 모임 권한"),
                                fieldWithPath("data.meetingApplications.[].roles.[].name").type(JsonFieldType.STRING).description("권한 명"),
                                fieldWithPath("data.meetingApplications.[].roles.[].roleGroupName").type(JsonFieldType.STRING).description("권한그룹 명"),
                                fieldWithPath("data.isCurrentUserRegMeeting").type(JsonFieldType.BOOLEAN).description("현재 접속한 유저가 해당 만남을 생성한 유저인지 여부"),
                                fieldWithPath("data.isCurrentUserApplicationMeeting").type(JsonFieldType.BOOLEAN).description("현재 접속한 유저가 해당 만남에 만남 신청을 했는지 여부"),
                                fieldWithPath("data.region").type(JsonFieldType.STRING).description("만남을 어느 지역에서 하는지 여부 (nullable)"),
                                fieldWithPath("data.regionURL").type(JsonFieldType.STRING).description("만남을 지역과 관련된 URL, 지도 URL등을 입력 가능 (nullable)"),
                                fieldWithPath("data.cost").type(JsonFieldType.NUMBER).description("만남 진행 시 필요한 금액(nullable)"),
                                fieldWithPath("data.isOpen").type(JsonFieldType.BOOLEAN).description("만남이 현재 활성상태인지 검사한다. 종료 시간을 기준으로 현재보다 만남 종료시간이 이후라면 활성상태로 판단한다."),
                                fieldWithPath("data.meetingDay").type(JsonFieldType.NUMBER).description("만남을 시작하는 날짜") ,
                                fieldWithPath("data.meetingDayOfWeek").type(JsonFieldType.STRING).description("만남을 시작하는 요일") ,
                                fieldWithPath("data.startDate").type(JsonFieldType.STRING).description("만남을 시작하는 날짜, $DATE_FORMAT_KOR") ,
                                fieldWithPath("data.startTime").type(JsonFieldType.STRING).description("만남을 시작하는 시간, $TIME_FORMAT") ,
                                fieldWithPath("data.endDate").type(JsonFieldType.STRING).description("만남을 종료하는 날짜, $DATE_FORMAT_KOR") ,
                                fieldWithPath("data.endTime").type(JsonFieldType.STRING).description("만남을 종료하는 시간, $TIME_FORMAT") ,

                            )
                ))


    }

    @Test
    @WithMockUser(authorities = [Role.MEMBER, Role.MANAGER])
    fun `만남 수정`() {




        given(clubService.getClubUser(ArgumentMatchers.anyLong(), MockitoHelper.anyObject())).willReturn(clubUser)
        given(roleService.hasClubManagerAuth(MockitoHelper.anyObject())).willReturn(true)
        given(meetingService.modifyMeeting(ArgumentMatchers.anyLong(), MockitoHelper.anyObject(), MockitoHelper.anyObject())).willReturn(meetingDto)
        given(meetingService.getMeeting(anyLong(), anyLong())).willReturn(meetingDto)

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
                                fieldWithPath("maximumNumber").type(JsonFieldType.NUMBER).description("변경할 만남 최대 인원"),
                                fieldWithPath("region").type(JsonFieldType.STRING).description("변경할 만남의 만남 장소(nullable)"),
                                fieldWithPath("regionURL").type(JsonFieldType.STRING).description("만남을 지역과 관련된 URL, 지도 URL등을 입력 가능 (nullable)"),
                                fieldWithPath("cost").type(JsonFieldType.NUMBER).description("변경할 만남의 진행 비용(nullable)")
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
                                fieldWithPath("data.meetingApplications.[].roles.[]").type(JsonFieldType.ARRAY).description("신청자의 모임 권한"),
                                fieldWithPath("data.meetingApplications.[].roles.[].name").type(JsonFieldType.STRING).description("권한 명"),
                                fieldWithPath("data.meetingApplications.[].roles.[].roleGroupName").type(JsonFieldType.STRING).description("권한그룹 명"),
                                fieldWithPath("data.isCurrentUserRegMeeting").type(JsonFieldType.BOOLEAN).description("현재 접속한 유저가 해당 만남을 생성한 유저인지 여부"),
                                fieldWithPath("data.isCurrentUserApplicationMeeting").type(JsonFieldType.BOOLEAN).description("현재 접속한 유저가 해당 만남에 만남 신청을 했는지 여부"),
                                fieldWithPath("data.region").type(JsonFieldType.STRING).description("만남을 어느 지역에서 하는지 여부 (nullable)"),
                                fieldWithPath("data.regionURL").type(JsonFieldType.STRING).description("만남을 지역과 관련된 URL, 지도 URL등을 입력 가능 (nullable)"),
                                fieldWithPath("data.cost").type(JsonFieldType.NUMBER).description("만남 진행 시 필요한 금액(nullable)"),
                                fieldWithPath("data.isOpen").type(JsonFieldType.BOOLEAN).description("만남이 현재 활성상태인지 검사한다. 종료 시간을 기준으로 현재보다 만남 종료시간이 이후라면 활성상태로 판단한다."),
                                fieldWithPath("data.meetingDay").type(JsonFieldType.NUMBER).description("만남을 시작하는 날짜") ,
                                fieldWithPath("data.meetingDayOfWeek").type(JsonFieldType.STRING).description("만남을 시작하는 요일") ,
                                fieldWithPath("data.startDate").type(JsonFieldType.STRING).description("만남을 시작하는 날짜, $DATE_FORMAT_KOR") ,
                                fieldWithPath("data.startTime").type(JsonFieldType.STRING).description("만남을 시작하는 시간, $TIME_FORMAT") ,
                                fieldWithPath("data.endDate").type(JsonFieldType.STRING).description("만남을 종료하는 날짜, $DATE_FORMAT_KOR") ,
                                fieldWithPath("data.endTime").type(JsonFieldType.STRING).description("만남을 종료하는 시간, $TIME_FORMAT") ,

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
                                fieldWithPath("data.meetingApplications.[].roles.[]").type(JsonFieldType.ARRAY).description("신청자의 모임 권한"),
                                fieldWithPath("data.meetingApplications.[].roles.[].name").type(JsonFieldType.STRING).description("권한 명"),
                                fieldWithPath("data.meetingApplications.[].roles.[].roleGroupName").type(JsonFieldType.STRING).description("권한그룹 명"),
                                fieldWithPath("data.isCurrentUserRegMeeting").type(JsonFieldType.BOOLEAN).description("현재 접속한 유저가 해당 만남을 생성한 유저인지 여부"),
                                fieldWithPath("data.isCurrentUserApplicationMeeting").type(JsonFieldType.BOOLEAN).description("현재 접속한 유저가 해당 만남에 만남 신청을 했는지 여부"),
                                fieldWithPath("data.region").type(JsonFieldType.STRING).description("만남을 어느 지역에서 하는지 여부 (nullable)"),
                                fieldWithPath("data.regionURL").type(JsonFieldType.STRING).description("만남을 지역과 관련된 URL, 지도 URL등을 입력 가능 (nullable)"),
                                fieldWithPath("data.cost").type(JsonFieldType.NUMBER).description("만남 진행 시 필요한 금액(nullable)"),
                                fieldWithPath("data.isOpen").type(JsonFieldType.BOOLEAN).description("만남이 현재 활성상태인지 검사한다. 종료 시간을 기준으로 현재보다 만남 종료시간이 이후라면 활성상태로 판단한다."),
                                fieldWithPath("data.meetingDay").type(JsonFieldType.NUMBER).description("만남을 시작하는 날짜") ,
                                fieldWithPath("data.meetingDayOfWeek").type(JsonFieldType.STRING).description("만남을 시작하는 요일") ,
                                fieldWithPath("data.startDate").type(JsonFieldType.STRING).description("만남을 시작하는 날짜, $DATE_FORMAT_KOR") ,
                                fieldWithPath("data.startTime").type(JsonFieldType.STRING).description("만남을 시작하는 시간, $TIME_FORMAT") ,
                                fieldWithPath("data.endDate").type(JsonFieldType.STRING).description("만남을 종료하는 날짜, $DATE_FORMAT_KOR") ,
                                fieldWithPath("data.endTime").type(JsonFieldType.STRING).description("만남을 종료하는 시간, $TIME_FORMAT") ,

                            )
                    )
                )
    }


}