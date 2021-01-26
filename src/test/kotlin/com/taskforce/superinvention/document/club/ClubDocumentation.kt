package com.taskforce.superinvention.document.club

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.interest.ClubInterest
import com.taskforce.superinvention.app.domain.interest.interest.Interest
import com.taskforce.superinvention.app.domain.interest.interest.InterestDto
import com.taskforce.superinvention.app.domain.interest.interestGroup.InterestGroup
import com.taskforce.superinvention.app.domain.interest.interestGroup.SimpleInterestGroupDto
import com.taskforce.superinvention.app.domain.region.ClubRegion
import com.taskforce.superinvention.app.domain.region.Region
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.role.RoleGroup
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.*
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.interest.InterestWithPriorityDto
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto
import com.taskforce.superinvention.app.web.dto.region.RegionWithPriorityDto
import com.taskforce.superinvention.app.web.dto.region.SimpleRegionDto
import com.taskforce.superinvention.app.web.dto.role.RoleDto
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.test.ApiDocumentationTest
import com.taskforce.superinvention.config.MockitoHelper
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonResponseField
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.pageFieldDescriptor
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.`when`
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ClubDocumentation: ApiDocumentationTest() {

    @Test
    @WithMockUser(authorities = [Role.MEMBER])
    fun `모임 생성`() {
        val clubAddRequestDto = ClubAddRequestDto(
                name = "땔감 스터디",
                description = "땔깜중에서도 고오급 땔깜이 되기 위해 노력하는 스터디",
                maximumNumber = 5L,
                mainImageUrl = "s3urlhost/d2e4dxxadf2E.png",
                interestList = listOf(InterestRequestDto(3L, 1L), InterestRequestDto(6L, 2L)),
                regionList = listOf(RegionRequestDto(101L, 1L), RegionRequestDto(102L, 2L))
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
                        document("addClub",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        fieldWithPath("name").type(JsonFieldType.STRING).description("모임명"),
                                        fieldWithPath("description").type(JsonFieldType.STRING).description("모임 설명"),
                                        fieldWithPath("maximumNumber").type(JsonFieldType.NUMBER).description("모임 최대 인원 수(변경 가능)"),
                                        fieldWithPath("mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지 url (Nullable)"),
                                        fieldWithPath("interestList").type(JsonFieldType.ARRAY)
                                                .description("모임 관심사. 반드시 1개 이상이어야 하며 우선순위가 1인 관심사가 하나 있어야한다. 0개 또는 2개 이상일 수 없다."),
                                        fieldWithPath("interestList[].seq").type(JsonFieldType.NUMBER).description("관심사 시퀀스"),
                                        fieldWithPath("interestList[].priority").type(JsonFieldType.NUMBER).description("관심사 우선순위. 우선순위가 1인 관심사가 반드시 하나 필요하다."),
                                        fieldWithPath("regionList").type(JsonFieldType.ARRAY)
                                                .description("모임이 활동하는 지역. 1개 이상이어야하며 우선순위가 1인 지역이 하나 있어야한다."),
                                        fieldWithPath("regionList[].seq").type(JsonFieldType.NUMBER).description("지역 시퀀스"),
                                        fieldWithPath("regionList[].priority").type(JsonFieldType.NUMBER).description("활동 지역 우선순위")
                                ),
                                responseFields(
                                        *commonResponseField()
                                )
                        )
                )
    }

    @Test
    @WithMockUser(authorities = [Role.MEMBER])
    fun `모임 가입`() {

        `when`(clubService.getValidClubBySeq(232))
                .thenReturn(Club(
                        "가상 모임",
                        "가상 모임에 대한 설명",
                        100L,
                        null
                ).apply { seq = 232 })

        val result = mockMvc.perform(
                post("/clubs/{clubSeq}/users", 232)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .characterEncoding("UTF-8")
        ).andDo(print())

        result.andExpect(status().isCreated)
                .andDo(
                        document("addClubUser",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                pathParameters(
                                        parameterWithName("clubSeq").description("모임 시퀀스.")
                                ),
                                responseFields(
                                        *commonResponseField()
                                )
                        )
                )
    }

    @Test
    @WithMockUser(authorities = [Role.MEMBER])
    fun `모임 리스트 조회`() {
        // given
        val searchResult = listOf(
                ClubWithRegionInterestDto(
                        seq = 6023L,
                        name = "산타 아저씨들",
                        description = "산이 너무 좋은 사람들의 모임입니다. 매주 정모 필참! 정모 후 뒷풀이는 선택." +
                                "많이 가입해주세요~",
                        maximumNumber = 300L,
                        userCount = 42L,
                        mainImageUrl = "taskforce-file-server/Exv2Es.png",
                        interests = listOf(
                                InterestWithPriorityDto(InterestDto(seq = 1, name = "운동", interestGroup = SimpleInterestGroupDto(1L, "운동/스포츠")), 1),
                                InterestWithPriorityDto(InterestDto(seq = 2, name = "건강", interestGroup = SimpleInterestGroupDto(1L, "운동/스포츠")), 2)
                        ),
                        regions = listOf(
                                RegionWithPriorityDto(SimpleRegionDto(seq = 101, name = "강남구", superRegionRoot = "서울특별시/강남구", level = 2), 1),
                                RegionWithPriorityDto(SimpleRegionDto(seq = 102, name = "강서구", superRegionRoot = "서울특별시/강서구", level = 2), 2)
                        )
                ),
                ClubWithRegionInterestDto(
                        seq = 45128989L,
                        name = "헬린이에서 근돼까지",
                        description = "와우 친구들 헬스장 아저씨야. " +
                                "3대 500 이상 착용할 수 있는 언더아머를 착용할 수 있을때 까지 힘내보자구!",
                        maximumNumber = 50L,
                        userCount = 1L,
                        mainImageUrl = "taskforce-file-server/0xV12v2Es.png",
                        interests = listOf(
                                InterestWithPriorityDto(InterestDto(seq = 1, name = "운동", interestGroup = SimpleInterestGroupDto(1L, "운동/스포츠")), 1),
                                InterestWithPriorityDto(InterestDto(seq = 2, name = "건강", interestGroup = SimpleInterestGroupDto(1L, "운동/스포츠")), 2)
                        ),
                        regions = listOf(
                                RegionWithPriorityDto(SimpleRegionDto(seq = 101, name = "강남구", superRegionRoot = "서울특별시/강남구", level = 2), 1),
                                RegionWithPriorityDto(SimpleRegionDto(seq = 102, name = "강서구", superRegionRoot = "서울특별시/강서구", level = 2), 2)
                        )
                )
        )

        `when`(clubService.search(MockitoHelper.anyObject(), MockitoHelper.anyObject()))
            .thenReturn(
                PageDto(
                    PageImpl(searchResult, PageRequest.of(0, 20), 203L)
                )
            )

        // when
        val result = mockMvc.perform(
                get("/clubs/search")
                        .queryParam("regionSeq", "101")
                        .queryParam("interestSeq", "1")
                        .queryParam("text", "검색 예시")
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .characterEncoding("UTF-8")
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
                .andDo(
                        document("searchClub", getDocumentRequest(), getDocumentResponse(),
                                requestParameters(
                                        // query string places like this
                                        parameterWithName("regionSeq").description("지역 seq"),
                                        parameterWithName("interestSeq").description("관심사 seq, 관심사 그룹으로 검색하고 싶다면 'interestGroupSeq'로 검색 가능."),
                                        parameterWithName("text").description("검색어 (제목, 모임 설명에 포함된 내용 검색 가능)"),
                                        parameterWithName("page").description("페이지"),
                                        parameterWithName("size").description("페이지당 사이즈")
                                ),
                                responseFields(
                                        *commonResponseField(),
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
                                        fieldWithPath("data.content[].interests[].interest.interestGroup").type(JsonFieldType.OBJECT).description("관심사 그룹 정보"),
                                        fieldWithPath("data.content[].interests[].interest.interestGroup.seq").type(JsonFieldType.NUMBER).description("관심사 그룹 시퀀스"),
                                        fieldWithPath("data.content[].interests[].interest.interestGroup.name").type(JsonFieldType.STRING).description("관심사 그룹 이름"),
                                        fieldWithPath("data.content[].interests[].priority").type(JsonFieldType.NUMBER).description("관심사 우선순위"),
                                        fieldWithPath("data.content[].regions").type(JsonFieldType.ARRAY).description("모임 참여지역"),
                                        fieldWithPath("data.content[].regions[].region").type(JsonFieldType.OBJECT).description("모임 지역 정보"),
                                        fieldWithPath("data.content[].regions[].region.seq").type(JsonFieldType.NUMBER).description("모임 지역 시퀀스"),
                                        fieldWithPath("data.content[].regions[].region.name").type(JsonFieldType.STRING).description("모임 지역 이름"),
                                        fieldWithPath("data.content[].regions[].region.superRegionRoot").type(JsonFieldType.STRING).description("모임 지역의 풀네임"),
                                        fieldWithPath("data.content[].regions[].region.level").type(JsonFieldType.NUMBER).description("모임 지역 뎁스 레벨"),
                                        fieldWithPath("data.content[].regions[].priority").type(JsonFieldType.NUMBER).description("모임 지역 우선 순위"),

                                        *pageFieldDescriptor()
                                )
                        )
                )
    }

    @Test
    @WithMockUser(authorities = [Role.MEMBER])
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
        ).apply { seq = clubSeq }

        val interestGroup = InterestGroup("건강").apply { seq = 1 }

        val interest3 = Interest(
                "헬스",
                interestGroup = interestGroup
        ).apply { seq = 3 }
        val interest5 = Interest(
                "운동",
                interestGroup = interestGroup
        ).apply { seq = 5 }

        val clubInterest1 = ClubInterest(
                club = club,
                interest = interest3,
                priority = 1
        ).apply { seq = 12451 }
        val clubInterest2 = ClubInterest(
                club = club,
                interest = interest5,
                priority = 1
        ).apply { seq = 12466 }

        club.clubInterests = listOf(
                clubInterest1,
                clubInterest2
        )


        val region1 = Region(
                superRegion = null,
                name = "성남시",
                superRegionRoot = "경기도/성남시",
                level = 2
        ).apply { seq = 401 }

        val clubRegion = ClubRegion(club, region1, 1).apply { seq = 41231 }

        club.clubRegions = listOf(
                clubRegion
        )

        club.clubUser = listOf(
                ClubUser(club, User("유저 1"), false),
                ClubUser(club, User("유저 2"), false),
                ClubUser(club, User("유저 3"), false),
                ClubUser(club, User("유저 4"), false),
                ClubUser(club, User("유저 5"), false)
        )


        `when`(clubService.getClubWithPriorityDto(clubSeq))
                .thenReturn(ClubWithRegionInterestDto(club, 5L))

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
                            *commonResponseField(),

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
                            fieldWithPath("data.interests[].interest.interestGroup").type(JsonFieldType.OBJECT).description("관심사 그룹 정보"),
                            fieldWithPath("data.interests[].interest.interestGroup.seq").type(JsonFieldType.NUMBER).description("관심사 그룹 시퀀스"),
                            fieldWithPath("data.interests[].interest.interestGroup.name").type(JsonFieldType.STRING).description("관심사 그룹 이름"),
                            fieldWithPath("data.interests[].priority").type(JsonFieldType.NUMBER).description("관심사 우선순위"),
                            fieldWithPath("data.regions").type(JsonFieldType.ARRAY).description("모임 참여지역"),
                            fieldWithPath("data.regions[].region").type(JsonFieldType.OBJECT).description("모임 지역 정보"),
                            fieldWithPath("data.regions[].region.seq").type(JsonFieldType.NUMBER).description("모임 지역 시퀀스"),
                            fieldWithPath("data.regions[].region.name").type(JsonFieldType.STRING).description("모임 지역 이름"),
                            fieldWithPath("data.regions[].region.superRegionRoot").type(JsonFieldType.STRING).description("모임 지역의 풀네임"),
                            fieldWithPath("data.regions[].region.level").type(JsonFieldType.NUMBER).description("모임 지역 뎁스 레벨"),
                            fieldWithPath("data.regions[].priority").type(JsonFieldType.NUMBER).description("모임 지역 우선 순위")
                    )
            ))
    }

    @Test
    @WithMockUser(authorities = [Role.MEMBER])
    fun `모임내 내 정보 조회`() {
        `when`(clubService.getClubUserInfo(ArgumentMatchers.anyLong(), MockitoHelper.anyObject()))
                .thenReturn(ClubUserDto(
                        seq = 5615,
                        userSeq = 1,
                        club = ClubDto(
                                seq = 1231,
                                name = "떡볶이를 좋아하는 사람들의 모임",
                                userCount = 15,
                                description = "떡볶이가 좋아요",
                                maximumNumber = 100,
                                mainImageUrl = "asdasdasd/fc.jpeg"
                        ),
                        roles = setOf(RoleDto(Role.RoleName.MASTER, "USER_TYPE"))
                ))

        val result = mockMvc.perform(
                get("/clubs/{clubSeq}/my-info", 1231)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .characterEncoding("UTF-8")
        ).andDo(print())

        result.andExpect(status().isOk)
                .andDo(
                        document("getMyClubUserInfo", getDocumentRequest(), getDocumentResponse(),
                                pathParameters(parameterWithName("clubSeq").description("모임 시퀀스")),
                                responseFields(
                                        *commonResponseField(),
                                        fieldWithPath("data.seq").type(JsonFieldType.NUMBER).description("모임원 시퀀스"),
                                        fieldWithPath("data.userSeq").type(JsonFieldType.NUMBER).description("유저 시퀀스"),
                                        fieldWithPath("data.club").type(JsonFieldType.OBJECT).description("모임 정보"),
                                        fieldWithPath("data.club.seq").type(JsonFieldType.NUMBER).description("모임 시퀀스"),
                                        fieldWithPath("data.club.name").type(JsonFieldType.STRING).description("모임 이름"),
                                        fieldWithPath("data.club.description").type(JsonFieldType.STRING).description("모임 설명"),
                                        fieldWithPath("data.club.maximumNumber").type(JsonFieldType.NUMBER).description("모임 최대 인원"),
                                        fieldWithPath("data.club.userCount").type(JsonFieldType.NUMBER).description("모임 현재 인원"),
                                        fieldWithPath("data.club.mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지"),
                                        fieldWithPath("data.roles").type(JsonFieldType.ARRAY).description("모임원이 가진 권한"),
                                        fieldWithPath("data.roles[].name").type(JsonFieldType.STRING).description("권한 이름"),
                                        fieldWithPath("data.roles[].roleGroupName").type(JsonFieldType.STRING).description("권한 그룹 이름")
                                )
                        )
                )
    }

    @Test
    @WithMockUser(authorities = [Role.MEMBER])
    fun `모임원 권한 변경`() {
        // given
        val requestBody = setOf<String>("MANAGER")    // MANAGER

        val currentUser = ClubUser(
                Club(
                        name = "땔감 스터디",
                        description = "땔깜중에서도 고오급 땔깜이 되기 위해 노력하는 스터디",
                        maximumNumber = 5L,
                        mainImageUrl = "s3urlhost/d2e4dxxadf2E.png"
                ).apply { seq = 2 },
                User("eric").apply { 12 },
                false
        )

        val targetClub = Club(
                name = "땔감 스터디",
                description = "땔깜중에서도 고오급 땔깜이 되기 위해 노력하는 스터디",
                maximumNumber = 5L,
                mainImageUrl = "s3urlhost/d2e4dxxadf2E.png"
        ).apply { seq = 91 }

        val targetUser = ClubUser(
                targetClub,
                User("sight studio"),
                false
        )

        `when`(clubService.getClubUser(ArgumentMatchers.anyLong(), MockitoHelper.anyObject()))
                .thenReturn(currentUser)

        `when`(roleService.hasClubMasterAuth(currentUser)).thenReturn(true)
        `when`(roleService.hasClubMasterAuth(targetUser)).thenReturn(false)

        `when`(clubService.getClubUserByClubUserSeq(ArgumentMatchers.anyLong()))
                .thenReturn(targetUser)

        `when`(roleService.findByRoleNameIn(MockitoHelper.anyObject()))
                .thenReturn(setOf(Role(Role.RoleName.CLUB_MEMBER, RoleGroup("USER_AUTH", "USER_AUTH"), 2)))

        val result = mockMvc.perform(
                put("/clubs/{clubSeq}/users/{clubUserSeq}/roles", 91, 3123)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(requestBody))
        ).andDo(print())

        result.andExpect(status().isOk)
                .andDo(
                        document("changeClubUserRole", getDocumentRequest(), getDocumentResponse(),
                                pathParameters(parameterWithName("clubSeq").description("모임 시퀀스"),
                                        parameterWithName("clubUserSeq").description("모임원 시퀀스")),
                                requestFields(
                                    fieldWithPath("[]").type(JsonFieldType.ARRAY).description("권한 코드")
                                ),
                                responseFields(
                                    *commonResponseField()
                                )
                        )
                )


    }

    @Test
    @WithMockUser(authorities = [Role.MEMBER])
    fun `내 모임 리스트 조회`() {

        // given
        val pageable:Pageable =  PageRequest.of(0, 20)
        val club = Club(
                name = "땔감 스터디",
                description = "땔깜중에서도 고오급 땔깜이 되기 위해 노력하는 스터디",
                maximumNumber = 5L,
                mainImageUrl = "s3urlhost/d2e4dxxadf2E.png"
        ).apply {
            seq = 123123
            userCount = 2
        }
        val user = User("sight studio").apply { seq = 1111 }

        val clubRegionList = listOf(RegionWithPriorityDto(SimpleRegionDto(seq = 101, name = "강남구", superRegionRoot = "서울특별시/강남구", level = 2), 1))
        val clubInterestList = listOf(InterestWithPriorityDto(InterestDto(11, "등산", SimpleInterestGroupDto(20, "운동/건강")), 2))

        `when`(clubService.getUserClubList(MockitoHelper.anyObject(), MockitoHelper.anyObject())).thenReturn(
            PageDto(
                PageImpl(listOf(
                                ClubUserWithClubDetailsDto (
                                        clubUserDto = ClubUserDto(
                                                seq = 12311,
                                                club = ClubDto(club),
                                                userSeq = 1,
                                            roles = setOf(RoleDto(Role.RoleName.MEMBER, "USER_TYPE"))
                                        ),
                                        interests = clubInterestList,
                                        regions   = clubRegionList
                                ),

                                ClubUserWithClubDetailsDto (
                                        clubUserDto = ClubUserDto(
                                                seq = 5615,
                                                userSeq = 1,
                                                club = ClubDto(
                                                        seq = 1231,
                                                        name = "떡볶이를 좋아하는 사람들의 모임",
                                                        userCount = 15,
                                                        description = "떡볶이가 좋아요",
                                                        maximumNumber = 100,
                                                        mainImageUrl = "asdasdasd/fc.jpeg"
                                                ),
                                                roles = setOf(RoleDto(Role.RoleName.MASTER, "USER_TYPE"))
                                        ),
                                        interests = clubInterestList,
                                        regions   = clubRegionList
                                )
                        ),
                        pageable, 2
                    )
                )
            )

        val result = mockMvc.perform(
                get("/clubs/my")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .characterEncoding("UTF-8")
        ).andDo(print())

        result.andExpect(status().isOk)
                .andDo(
                        document("myClubList", getDocumentRequest(), getDocumentResponse(),
                                responseFields(
                                        *commonResponseField(),
                                        *pageFieldDescriptor(),
                                        fieldWithPath("data.content[].clubUserSeq").type(JsonFieldType.NUMBER).description("모임원 시퀀스"),
                                        fieldWithPath("data.content[].userSeq").type(JsonFieldType.NUMBER).description("유저 시퀀스"),
                                        fieldWithPath("data.content[].club").type(JsonFieldType.OBJECT).description("모임 정보"),
                                        fieldWithPath("data.content[].club.seq").type(JsonFieldType.NUMBER).description("모임 시퀀스"),
                                        fieldWithPath("data.content[].club.name").type(JsonFieldType.STRING).description("모임 제목"),
                                        fieldWithPath("data.content[].club.description").type(JsonFieldType.STRING).description("모임 설명"),
                                        fieldWithPath("data.content[].club.maximumNumber").type(JsonFieldType.NUMBER).description("최대 가입 가능 인원"),
                                        fieldWithPath("data.content[].club.userCount").type(JsonFieldType.NUMBER).description("현재 가입 인원"),
                                        fieldWithPath("data.content[].club.mainImageUrl").type(JsonFieldType.STRING).description("모임 메인 이미지 URL"),
                                        fieldWithPath("data.content[].roles").type(JsonFieldType.ARRAY).description("모임원 권한 정보"),
                                        fieldWithPath("data.content[].roles[].name").type(JsonFieldType.STRING).description("권한 이름"),
                                        fieldWithPath("data.content[].roles[].roleGroupName").type(JsonFieldType.STRING).description("권한 그룹 이름"),

                                        fieldWithPath("data.content[].club.clubInterest[].interest.seq").type(JsonFieldType.NUMBER).description("모임 관심사 seq"),
                                        fieldWithPath("data.content[].club.clubInterest[].interest.name").type(JsonFieldType.STRING).description("모임 관심사명"),
                                        fieldWithPath("data.content[].club.clubInterest[].interest.interestGroup.seq").type(JsonFieldType.NUMBER).description("모임 관심사 그룹 Seq"),
                                        fieldWithPath("data.content[].club.clubInterest[].interest.interestGroup.name").type(JsonFieldType.STRING).description("모임 관심사 그룹명"),
                                        fieldWithPath("data.content[].club.clubInterest[].priority").type(JsonFieldType.NUMBER).description("모임 관심사 우선순위"),
                                        fieldWithPath("data.content[].club.clubRegion[].seq").type(JsonFieldType.NUMBER).description("모임 지역 seq"),
                                        fieldWithPath("data.content[].club.clubRegion[].name").type(JsonFieldType.STRING).description("모임 지역명"),
                                        fieldWithPath("data.content[].club.clubRegion[].superRegionRoot").type(JsonFieldType.STRING).description("모임 상위 지역"),
                                        fieldWithPath("data.content[].club.clubRegion[].level").type(JsonFieldType.NUMBER).description("모임 지역 단계")
                                )
                        )
                )
    }

    @Test
    @WithMockUser(authorities = [Role.MEMBER])
    fun `모임 지역 변경`() {
        // given
        val requestBody: Set<RegionRequestDto> = setOf(
                RegionRequestDto(401L, 1L),
                RegionRequestDto(123L, 2L)
        )

        val clubSeq = 57231L

        val club = Club(
                name = "땔감 스터디",
                description = "땔깜중에서도 고오급 땔깜이 되기 위해 노력하는 스터디",
                maximumNumber = 5L,
                mainImageUrl = "s3urlhost/d2e4dxxadf2E.png"
        ).apply { seq = clubSeq }

        val interestGroup = InterestGroup("건강").apply { seq = 1 }

        val interest3= Interest(
                "헬스",
                interestGroup = interestGroup
        ).apply { seq = 3 }
        val interest5 = Interest(
                "운동",
                interestGroup = interestGroup
        ).apply { seq = 5 }

        val clubInterest1 = ClubInterest(
                club = club,
                interest = interest3,
                priority = 1
        ).apply { seq = 12451 }
        val clubInterest2 = ClubInterest(
                club = club,
                interest = interest5,
                priority = 1
        ).apply { seq = 12466 }

        club.clubInterests = listOf(
                clubInterest1,
                clubInterest2
        )


        val region1 = Region(
                superRegion = null,
                name = "성남시",
                superRegionRoot = "경기도/성남시",
                level = 2
        ).apply { seq = 401; }

        val region2 = Region(
                superRegion = null,
                name = "강남구",
                level = 2,
                superRegionRoot = "서울특별시/강남구"
        ).apply { seq = 123; }

        val clubRegion = ClubRegion(club, region1, 1).apply { seq = 41231 }
        val clubRegion2 = ClubRegion(club, region2, 2).apply { seq = 41231 }

        club.clubRegions = listOf(
                clubRegion,
                clubRegion2
        )


        club.clubUser = listOf(
                ClubUser(club, User("유저 1"),false),
                ClubUser(club, User("유저 2"),false),
                ClubUser(club, User("유저 3"),false),
                ClubUser(club, User("유저 4"),false),
                ClubUser(club, User("유저 5"),false)
        )


        `when`(clubService.getClubWithPriorityDto(clubSeq))
                .thenReturn(ClubWithRegionInterestDto(club, 5L))

        `when`(roleService.hasClubManagerAuth(MockitoHelper.anyObject())).thenReturn(true)

        // when
        val result = mockMvc.perform(
                put("/clubs/{clubSeq}/regions", clubSeq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(requestBody))
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
                .andDo(document(
                        "changeClubRegions", getDocumentRequest(), getDocumentResponse(),
                        pathParameters(parameterWithName("clubSeq").description("모임 시퀀스. 해당 유저는 이 모임에 대해 매니저 이상의 권한을 가지고 있어야 한다.")),
                        requestFields(
                                fieldWithPath("[].seq").type(JsonFieldType.NUMBER).description("지역 시퀀스"),
                                fieldWithPath("[].priority").type(JsonFieldType.NUMBER).description("지역에 대한 우선순위")
                        ),
                        responseFields(
                                *commonResponseField(),

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
                                fieldWithPath("data.interests[].interest.interestGroup").type(JsonFieldType.OBJECT).description("관심사 그룹 정보"),
                                fieldWithPath("data.interests[].interest.interestGroup.seq").type(JsonFieldType.NUMBER).description("관심사 그룹 시퀀스"),
                                fieldWithPath("data.interests[].interest.interestGroup.name").type(JsonFieldType.STRING).description("관심사 그룹 이름"),
                                fieldWithPath("data.interests[].priority").type(JsonFieldType.NUMBER).description("관심사 우선순위"),
                                fieldWithPath("data.regions").type(JsonFieldType.ARRAY).description("모임 참여지역"),
                                fieldWithPath("data.regions[].region").type(JsonFieldType.OBJECT).description("모임 지역 정보"),
                                fieldWithPath("data.regions[].region.seq").type(JsonFieldType.NUMBER).description("모임 지역 시퀀스"),
                                fieldWithPath("data.regions[].region.name").type(JsonFieldType.STRING).description("모임 지역 이름"),
                                fieldWithPath("data.regions[].region.superRegionRoot").type(JsonFieldType.STRING).description("모임 지역의 풀네임"),
                                fieldWithPath("data.regions[].region.level").type(JsonFieldType.NUMBER).description("모임 지역 뎁스 레벨"),
                                fieldWithPath("data.regions[].priority").type(JsonFieldType.NUMBER).description("모임 지역 우선 순위")
                        )
                ))
    }
}