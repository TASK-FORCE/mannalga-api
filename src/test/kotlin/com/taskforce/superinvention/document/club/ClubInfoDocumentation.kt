package com.taskforce.superinvention.document.club

import com.ninjasquad.springmockk.MockkBean
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.interest.interest.InterestDto
import com.taskforce.superinvention.app.domain.interest.interestGroup.SimpleInterestGroupDto
import com.taskforce.superinvention.app.domain.region.Region
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.userInterest.UserInterestService
import com.taskforce.superinvention.app.web.dto.club.ClubInfoDetailsDto
import com.taskforce.superinvention.app.web.dto.club.ClubInfoDto
import com.taskforce.superinvention.app.web.dto.club.ClubUserStatusDto
import com.taskforce.superinvention.app.web.dto.interest.InterestWithPriorityDto
import com.taskforce.superinvention.app.web.dto.region.SimpleRegionDto
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonResponseField
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.test.ApiDocumentationTestV2
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class ClubInfoDocumentation: ApiDocumentationTestV2() {

    @MockkBean
    lateinit var clubService : ClubService

    @MockkBean
    lateinit var userInterestService: UserInterestService

    @MockkBean
    lateinit var roleService: RoleService

    lateinit var user: User
    lateinit var club: Club
    lateinit var clubUser: ClubUser
    lateinit var region: Region

    @BeforeEach
    fun setup() {
        user = User ("sight").apply { seq = 1 }
        club = Club(
                name = "가상 모임",
                description = "가상 모임에 대한 설명",
                maximumNumber = 100L,
                mainImageUrl = ""
        ).apply {
            seq = 2
            userCount = 2
        }

        clubUser = ClubUser(club, user, isLiked = false).apply {
            seq = 3
        }

        region = Region(
                superRegion = null,
                name = "성남시",
                superRegionRoot = "경기도/성남시",
                level = 2
        ).apply { seq = 401 }
    }


    @Test
    @WithMockUser(authorities = [ Role.MEMBER, Role.CLUB_MEMBER ])
    fun `모임 상세 조회`() {

        // given
        val clubRegionList = listOf(SimpleRegionDto(seq = 101, name = "강남구", superRegionRoot = "서울특별시/강남구", level = 2))
        val interestList = listOf(InterestWithPriorityDto(InterestDto(11, "등산", SimpleInterestGroupDto(20, "운동/건강")), 2))

        val resultDto = ClubInfoDetailsDto (
                clubInfo = ClubInfoDto(
                        club = club,
                        clubInterest = interestList,
                        clubRegion   = clubRegionList
                ),
                userInfo = ClubUserStatusDto(
                        role = listOf(Role.RoleName.CLUB_MEMBER, Role.RoleName.MANAGER),
                        isLiked = false
                )
        )

        // when
        every { clubService.getClubInfoDetail(any(), club.seq!!) }.returns(resultDto)

        val result = mockMvc.perform(
                get("/clubs/{clubSeq}", club.seq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .characterEncoding("UTF-8")
        ).andDo(print())

        result.andExpect(status().isOk)
                .andDo(
                        document("select-club-info-detail",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                pathParameters(parameterWithName("clubSeq").description("모임 시퀀스.")),
                                responseFields(
                                        *commonResponseField(),
                                        fieldWithPath("data.clubInfo.seq").type(JsonFieldType.NUMBER).description("모임 시퀀스"),
                                        fieldWithPath("data.clubInfo.name").type(JsonFieldType.STRING).description("모임 명"),
                                        fieldWithPath("data.clubInfo.description").type(JsonFieldType.STRING).description("모임 설명"),
                                        fieldWithPath("data.clubInfo.maximumNumber").type(JsonFieldType.NUMBER).description("모임 최대 가입 인원 수"),
                                        fieldWithPath("data.clubInfo.userCount").type(JsonFieldType.NUMBER).description("현재 모임원 인원수"),
                                        fieldWithPath("data.clubInfo.mainImageUrl").type(JsonFieldType.STRING).description("모임 대표 사진"),
                                        fieldWithPath("data.clubInfo.clubInterest[].interest.seq").type(JsonFieldType.NUMBER).description("모임 관심사 seq"),
                                        fieldWithPath("data.clubInfo.clubInterest[].interest.name").type(JsonFieldType.STRING).description("모임 관심사명"),
                                        fieldWithPath("data.clubInfo.clubInterest[].interest.interestGroup.seq").type(JsonFieldType.NUMBER).description("모임 관심사 그룹 Seq"),
                                        fieldWithPath("data.clubInfo.clubInterest[].interest.interestGroup.name").type(JsonFieldType.STRING).description("모임 관심사 그룹명"),
                                        fieldWithPath("data.clubInfo.clubInterest[].priority").type(JsonFieldType.NUMBER).description("모임 관심사 우선순위"),
                                        fieldWithPath("data.clubInfo.clubRegion[].seq").type(JsonFieldType.NUMBER).description("모임 지역 seq"),
                                        fieldWithPath("data.clubInfo.clubRegion[].name").type(JsonFieldType.STRING).description("모임 지역명"),
                                        fieldWithPath("data.clubInfo.clubRegion[].superRegionRoot").type(JsonFieldType.STRING).description("모임 상위 지역"),
                                        fieldWithPath("data.clubInfo.clubRegion[].level").type(JsonFieldType.NUMBER).description("모임 지역 단계"),
                                        fieldWithPath("data.userInfo.role[]").type(JsonFieldType.ARRAY).description("유저 권한"),
                                        fieldWithPath("data.userInfo.isLiked").type(JsonFieldType.BOOLEAN).description("모임원 모임 좋아요 여부")
                                )
                        )
                )
    }
}

