package com.taskforce.superinvention.document.club

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.ClubUser
import com.taskforce.superinvention.app.domain.interest.ClubInterest
import com.taskforce.superinvention.app.domain.interest.interest.Interest
import com.taskforce.superinvention.app.domain.interest.interest.InterestDto
import com.taskforce.superinvention.app.domain.interest.interestGroup.InterestGroup
import com.taskforce.superinvention.app.domain.state.ClubState
import com.taskforce.superinvention.app.domain.state.State
import com.taskforce.superinvention.app.domain.user.user.User
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.club.*
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.interest.InterestWithPriorityDto
import com.taskforce.superinvention.app.web.dto.state.SimpleStateDto
import com.taskforce.superinvention.app.web.dto.state.StateRequestDto
import com.taskforce.superinvention.app.web.dto.state.StateWithPriorityDto
import com.taskforce.superinvention.config.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.ApiDocumentationTest
import com.taskforce.superinvention.config.MockitoHelper
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ClubDocumentation: ApiDocumentationTest() {

    @Test
    @WithMockUser
    fun `모임 생성`() {
        val clubAddRequestDto = ClubAddRequestDto(
                name = "땔감 스터디",
                description = "땔깜중에서도 고오급 땔깜이 되기 위해 노력하는 스터디",
                maximumNumber = 5L,
                mainImageUrl = "s3urlhost/d2e4dxxadf2E.png",
                interestList = listOf(InterestRequestDto(3L, 1L), InterestRequestDto(6L, 2L)),
                stateList = listOf(StateRequestDto(101L, 1L), StateRequestDto(102L, 2L))
        )

        val result = mockMvc.perform(
                post("/clubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(clubAddRequestDto))
        ).andDo(print())

        result.andExpect(status().isCreated)
                .andDo(
                        document("addClub", getDocumentRequest(), getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("모임명"),
                                        fieldWithPath("description").type(JsonFieldType.STRING).description("모임 설명"),
                                        fieldWithPath("maximumNumber").type(JsonFieldType.NUMBER).description("모임 최대 인원 수(변경 가능)"),
                                        fieldWithPath("mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지 url (Nullable)"),
                                        fieldWithPath("interestList").type(JsonFieldType.ARRAY)
                                                .description("모임 관심사. 반드시 1개 이상이어야 하며 우선순위가 1인 관심사가 하나 있어야한다. 0개 또는 2개 이상일 수 없다."),
                                        fieldWithPath("interestList[].seq").type(JsonFieldType.NUMBER).description("관심사 시퀀스"),
                                        fieldWithPath("interestList[].priority").type(JsonFieldType.NUMBER).description("관심사 우선순위. 우선순위가 1인 관심사가 반드시 하나 필요하다."),
                                        fieldWithPath("stateList").type(JsonFieldType.ARRAY)
                                                .description("모임이 활동하는 지역. 1개 이상이어야하며 우선순위가 1인 지역이 하나 있어야한다."),
                                        fieldWithPath("stateList[].seq").type(JsonFieldType.NUMBER).description("지역 시퀀스"),
                                        fieldWithPath("stateList[].priority").type(JsonFieldType.NUMBER).description("활동 지역 우선순위")
                                )
                        )
                )
    }

    @Test
    @WithMockUser
    fun `모임 가입`() {

        `when`(clubService.getClubBySeq(232))
                .thenReturn(Club(
                        "가상 모임",
                        "가상 모임에 대한 설명",
                        100L,
                        null
                ))

        val result = mockMvc.perform(
                post("/clubs/232/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .characterEncoding("UTF-8")
        ).andDo(print())

        result.andExpect(status().isCreated)
                .andDo(
                        document("addClubUser", getDocumentRequest(), getDocumentResponse())
                )
    }

    @Test
    @WithMockUser
    fun `모임 리스트 조회`() {
        // given
        val searchResult = listOf(
                ClubWithStateInterestDto(
                        seq = 6023L,
                        name = "산타 아저씨들",
                        description = "산이 너무 좋은 사람들의 모임입니다. 매주 정모 필참! 정모 후 뒷풀이는 선택." +
                                "많이 가입해주세요~",
                        maximumNumber = 300L,
                        userCount = 42L,
                        mainImageUrl = "taskforce-file-server/Exv2Es.png",
                        interests = listOf(
                                InterestWithPriorityDto(InterestDto(seq = 1, name = "운동"), 1),
                                InterestWithPriorityDto(InterestDto(seq = 2, name = "건강"), 2)
                        ),
                        states = listOf(
                                StateWithPriorityDto(SimpleStateDto(seq = 101, name = "강남구", superStateRoot = "서울특별시/강남구", level = 2), 1),
                                StateWithPriorityDto(SimpleStateDto(seq = 102, name = "강서구", superStateRoot = "서울특별시/강서구", level = 2), 2)
                        )
                ),
                ClubWithStateInterestDto(
                        seq = 45128989L,
                        name = "헬린이에서 근돼까지",
                        description = "와우 친구들 헬스장 아저씨야. " +
                                "3대 500 이상 착용할 수 있는 언더아머를 착용할 수 있을때 까지 힘내보자구!",
                        maximumNumber = 50L,
                        userCount = 1L,
                        mainImageUrl = "taskforce-file-server/0xV12v2Es.png",
                        interests = listOf(
                                InterestWithPriorityDto(InterestDto(seq = 1, name = "운동"), 1),
                                InterestWithPriorityDto(InterestDto(seq = 2, name = "건강"), 2)
                        ),
                        states = listOf(
                                StateWithPriorityDto(SimpleStateDto(seq = 101, name = "강남구", superStateRoot = "서울특별시/강남구", level = 2), 1),
                                StateWithPriorityDto(SimpleStateDto(seq = 102, name = "강서구", superStateRoot = "서울특별시/강서구", level = 2), 2)
                        )
                )
        )

        val searchRequest = ClubSearchRequestDto(
                offset = 0L,
                size = 10L,
                searchOptions = ClubSearchOptions(
                        stateList = listOf(
                                StateRequestDto(
                                        seq = 102L,
                                        priority = 1L
                                )
                        ),
                        interestList = listOf(
                            InterestRequestDto(
                                    seq = 4L,
                                    priority = 1L
                            ),
                            InterestRequestDto(
                                    seq = 20L,
                                    priority = 2L
                            )
                        )
                )
        )

        `when`(clubService.search(MockitoHelper.anyObject())).thenReturn(PageImpl(searchResult, PageRequest.of(0, 10), 203L))

        // when
        val result = mockMvc.perform(
                post("/clubs/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(searchRequest))
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
                .andDo(
                        document("searchClub", getDocumentRequest(), getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("offset").type(JsonFieldType.NUMBER).description("요청하는 페이지"),
                                        fieldWithPath("size").type(JsonFieldType.NUMBER).description("한번에 조회할 모임 개수"),
                                        fieldWithPath("searchOptions").type(JsonFieldType.OBJECT).description("검색할 조건들(현재는 지역, 관심사를 검색 조건에 넣을 수 있음)"),
                                        fieldWithPath("searchOptions.stateList").type(JsonFieldType.ARRAY).description("검색할 지역 리스트 (비어있어도 된다)"),
                                        fieldWithPath("searchOptions.stateList[].seq").type(JsonFieldType.NUMBER).description("지역 시퀀스"),
                                        fieldWithPath("searchOptions.stateList[].priority").type(JsonFieldType.NUMBER).description("검색할 지역의 우선순위. 높을수록 해당 지역의 우선순위를 높게 설정하여 상단에 노출된다."),
                                        fieldWithPath("searchOptions.interestList[]").type(JsonFieldType.ARRAY).description("검색할 관심사 리스트 (비어있어도 된다)"),
                                        fieldWithPath("searchOptions.interestList[].seq").type(JsonFieldType.NUMBER).description("관심사 시퀀스"),
                                        fieldWithPath("searchOptions.interestList[].priority").type(JsonFieldType.NUMBER).description("검색할 관심사의 우선순위. 높을수록 해당 관심사의 우선순위를 높게 설정하여 상단에 노출된다.")
                                ),
                                responseFields(
                                        fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),

                                        fieldWithPath("data.content").type(JsonFieldType.ARRAY).description("데이터 본문"),
                                        fieldWithPath("data.content[].seq").type(JsonFieldType.NUMBER).description("모임의 시퀀스"),
                                        fieldWithPath("data.content[].name").type(JsonFieldType.STRING).description("모임명"),
                                        fieldWithPath("data.content[].description").type(JsonFieldType.STRING).description("모임에 대한 설명"),
                                        fieldWithPath("data.content[].maximumNumber").type(JsonFieldType.NUMBER).description("모임 최대 가입 인원수"),
                                        fieldWithPath("data.content[].userCount").type(JsonFieldType.NUMBER).description("현재 모임원 인원수"),
                                        fieldWithPath("data.content[].mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지 (Nullable)"),
                                        fieldWithPath("data.content[].interests").type(JsonFieldType.ARRAY).description("모임이 추구하는 관심사"),
                                        fieldWithPath("data.content[].interests[].interest").type(JsonFieldType.OBJECT).description("모임 관심사 정보"),
                                        fieldWithPath("data.content[].interests[].interest.seq").type(JsonFieldType.NUMBER).description("모임 관심사 시퀀스"),
                                        fieldWithPath("data.content[].interests[].interest.name").type(JsonFieldType.STRING).description("모임 관심사 이름"),
                                        fieldWithPath("data.content[].interests[].priority").type(JsonFieldType.NUMBER).description("관심사 우선순위"),
                                        fieldWithPath("data.content[].states").type(JsonFieldType.ARRAY).description("모임 참여지역"),
                                        fieldWithPath("data.content[].states[].state").type(JsonFieldType.OBJECT).description("모임 지역 정보"),
                                        fieldWithPath("data.content[].states[].state.seq").type(JsonFieldType.NUMBER).description("모임 지역 시퀀스"),
                                        fieldWithPath("data.content[].states[].state.name").type(JsonFieldType.STRING).description("모임 지역 이름"),
                                        fieldWithPath("data.content[].states[].state.superStateRoot").type(JsonFieldType.STRING).description("모임 지역의 풀네임"),
                                        fieldWithPath("data.content[].states[].state.level").type(JsonFieldType.NUMBER).description("모임 지역 뎁스 레벨"),
                                        fieldWithPath("data.content[].states[].priority").type(JsonFieldType.NUMBER).description("모임 지역 우선 순위"),

                                        fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이징 정보"),

                                        fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                                        fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                        fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 안했는지 여부"),
                                        fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("빈 데이터인지 여부"),

                                        fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("요청한 오프셋(몇 페이지인지, 0부터 시작)"),
                                        fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("몇 번째 페이지인지"),
                                        fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("요청한 페이지의 사이즈"),
                                        fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이징을 하지 않았는지 여부"),
                                        fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),

                                        fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("총 데이터 개수"),
                                        fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지인지 여부"),
                                        fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("요청한 오프셋으로 페이징할 때 총 페이지 개수"),
                                        fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 넘버"),
                                        fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이징된 데이터 개수"),
                                        fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
                                        fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
                                        fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 안했는지 여부"),
                                        fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("빈 데이터인지 여부"),

                                        fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("응답 데이터 개수"),
                                        fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫번째 페이지인지 여부"),
                                        fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("페이지가 비어있는지 여부"),


                                        fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지")
                                )
                        )
                )
    }

    @Test
    @WithMockUser
    fun `모임 관심사 변경`() {
        // given
        val requestBody: Set<InterestRequestDto> = setOf(
                InterestRequestDto(seq = 3, priority = 1),
                InterestRequestDto(seq = 5, priority = 2)
        )

        val clubSeq = 57231L

        val club = Club(
                name = "땔감 스터디",
                description = "땔깜중에서도 고오급 땔깜이 되기 위해 노력하는 스터디",
                maximumNumber = 5L,
                mainImageUrl = "s3urlhost/d2e4dxxadf2E.png"
        )
        club.seq = clubSeq

        val interest3 = Interest(
                "헬스",
                interestGroup = InterestGroup(
                        "건강",
                        listOf()
                )
        )
        val interest5 = Interest(
                "운동",
                interestGroup = InterestGroup(
                        "건강",
                        listOf()
                )
        )

        val clubInterest1 = ClubInterest(
                club = club,
                interest = interest3,
                priority = 1
        )
        val clubInterest2 = ClubInterest(
                club = club,
                interest = interest5,
                priority = 1
        )
        clubInterest1.seq = 12451
        interest3.seq = 3
        clubInterest2.seq = 12466
        interest5.seq = 5

        club.clubInterests = listOf(
                clubInterest1,
                clubInterest2
        )


        val state1 = State(
                superState = null,
                name = "성남시",
                superStateRoot = "경기도/성남시",
                level = 2,
                subStates = listOf()
        )
        state1.seq = 401

        val clubState = ClubState(club, state1, 1)
        clubState.seq = 41231


        club.clubStates = listOf(
                clubState
        )


        club.clubUser = listOf(
                ClubUser(club, User("유저 1")),
                ClubUser(club, User("유저 2")),
                ClubUser(club, User("유저 3")),
                ClubUser(club, User("유저 4")),
                ClubUser(club, User("유저 5"))
        )


        `when`(clubService.getClubWithPriorityDto(clubSeq))
                .thenReturn(ClubWithStateInterestDto(club, 5L))

        `when`(roleService.hasClubManagerAuth(MockitoHelper.anyObject())).thenReturn(true)

        // when
        val result = mockMvc.perform(
                put("/clubs/{clubSeq}/interests", clubSeq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(requestBody))
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
            .andDo(document(
                    "changeClubInterests", getDocumentRequest(), getDocumentResponse(),
                    pathParameters(parameterWithName("clubSeq").description("모임 시퀀스. 해당 유저는 이 모임에 대해 매니저 이상의 권한을 가지고 있어야 한다.")),
                    requestFields(
                            fieldWithPath("[].seq").type(JsonFieldType.NUMBER).description("관심사 시퀀스"),
                            fieldWithPath("[].priority").type(JsonFieldType.NUMBER).description("관심사에 대한 우선순위")
                    ),
                    responseFields(
                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),

                            fieldWithPath("data").type(JsonFieldType.OBJECT).description("데이터 본문"),
                            fieldWithPath("data.seq").type(JsonFieldType.NUMBER).description("모임의 시퀀스"),
                            fieldWithPath("data.name").type(JsonFieldType.STRING).description("모임명"),
                            fieldWithPath("data.description").type(JsonFieldType.STRING).description("모임에 대한 설명"),
                            fieldWithPath("data.maximumNumber").type(JsonFieldType.NUMBER).description("모임 최대 가입 인원수"),
                            fieldWithPath("data.userCount").type(JsonFieldType.NUMBER).description("현재 모임원 인원수"),
                            fieldWithPath("data.mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지 (Nullable)"),
                            fieldWithPath("data.interests").type(JsonFieldType.ARRAY).description("모임이 추구하는 관심사"),
                            fieldWithPath("data.interests[].interest").type(JsonFieldType.OBJECT).description("모임 관심사 정보"),
                            fieldWithPath("data.interests[].interest.seq").type(JsonFieldType.NUMBER).description("모임 관심사 시퀀스"),
                            fieldWithPath("data.interests[].interest.name").type(JsonFieldType.STRING).description("모임 관심사 이름"),
                            fieldWithPath("data.interests[].priority").type(JsonFieldType.NUMBER).description("관심사 우선순위"),
                            fieldWithPath("data.states").type(JsonFieldType.ARRAY).description("모임 참여지역"),
                            fieldWithPath("data.states[].state").type(JsonFieldType.OBJECT).description("모임 지역 정보"),
                            fieldWithPath("data.states[].state.seq").type(JsonFieldType.NUMBER).description("모임 지역 시퀀스"),
                            fieldWithPath("data.states[].state.name").type(JsonFieldType.STRING).description("모임 지역 이름"),
                            fieldWithPath("data.states[].state.superStateRoot").type(JsonFieldType.STRING).description("모임 지역의 풀네임"),
                            fieldWithPath("data.states[].state.level").type(JsonFieldType.NUMBER).description("모임 지역 뎁스 레벨"),
                            fieldWithPath("data.states[].priority").type(JsonFieldType.NUMBER).description("모임 지역 우선 순위"),

                            fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지")
                    )
            ))
    }
}