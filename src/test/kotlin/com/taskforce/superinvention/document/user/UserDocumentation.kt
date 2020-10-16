package com.taskforce.superinvention.document.user

import com.taskforce.superinvention.app.domain.interest.interest.Interest
import com.taskforce.superinvention.app.domain.interest.interestGroup.InterestGroup
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.region.Region
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.UserInfoService
import com.taskforce.superinvention.app.domain.user.userInterest.UserInterest
import com.taskforce.superinvention.common.config.security.AppToken
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.kakao.*
import com.taskforce.superinvention.app.web.dto.region.*
import com.taskforce.superinvention.app.web.dto.user.UserRegionDto
import com.taskforce.superinvention.app.web.dto.user.info.UserInfoDto
import com.taskforce.superinvention.app.web.dto.user.info.UserInfoInterestDto
import com.taskforce.superinvention.app.web.dto.user.info.UserInfoRegionDto
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.test.ApiDocumentationTest
import com.taskforce.superinvention.config.MockitoHelper.anyObject
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonResponseField
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
import org.mockito.Mock
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDate

class UserDocumentation : ApiDocumentationTest() {

    @Test
    fun `유저 최초 로그인 처리`() {

        // given
        val kakoToken = KakaoToken(
                token_type    = "",
                access_token  = "xxxxxxxxx",
                expireds_in   = 3600, // 초 단위
                refresh_token = "xxxxxxxxx",
                refresh_token_expires_in = 3600
        )

        val appToken = AppToken(
                isRegistered = true,
                appToken = "xxxxxxxxxx"
        )

        `when`(userService.saveKakaoToken(anyObject())).thenReturn(appToken)

        // when
        val result: ResultActions = this.mockMvc.perform(
                post("/users/saveKakaoToken")
                        .content(objectMapper.writeValueAsString(kakoToken))
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
                .andDo(document("saveKakaoToken", getDocumentRequest(), getDocumentResponse(),
                        requestFields(
                                fieldWithPath("token_type").type(JsonFieldType.STRING).description("[optional] 토큰 타입 (사용x)"),
                                fieldWithPath("access_token").type(JsonFieldType.STRING).description("oauth 엑세스 토큰"),
                                fieldWithPath("expireds_in").type(JsonFieldType.NUMBER).description("엑세스 토큰 TTL (초)"),
                                fieldWithPath("refresh_token").type(JsonFieldType.STRING).description("oauth 리프래스 토큰"),
                                fieldWithPath("refresh_token_expires_in").type(JsonFieldType.NUMBER).description("리프래스 토큰 TTL (초)")
                        ),
                        responseFields(
                                *commonResponseField(),
                                fieldWithPath("data.isRegistered").type(JsonFieldType.BOOLEAN).description("최초 회원가입 여부"),
                                fieldWithPath("data.appToken").type(JsonFieldType.STRING).description("앱 JWT 토큰")
                        )
                ))
    }

    @Test
    @WithMockUser(authorities = [Role.NONE])
    fun `유저 등록`() {

        // given
        val kakaoUserRegisterDto = KakaoUserRegistRequest(
                userName = "에릭",
                birthday = LocalDate.parse("1995-12-27"),
                profileImageLink = "",
                userRegions = listOf<RegionRequestDto>(RegionRequestDto(seq = 201, priority = 1), RegionRequestDto(seq = 202, priority = 2)),
                userInterests = listOf<InterestRequestDto>(InterestRequestDto(seq = 1, priority = 1), InterestRequestDto(seq = 2, priority = 2))
        )

        // when
        val result = this.mockMvc.perform(
                post("/users/regist")
                        .header("Authorization", "Bearer ACACACACACAXCZCZXCXZ")
                        .content(objectMapper.writeValueAsString(kakaoUserRegisterDto))
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isCreated)
                .andDo(document("userRegist", getDocumentRequest(), getDocumentResponse(),
                        requestFields(
                                fieldWithPath("userName").type(JsonFieldType.STRING).description("유저 이름(닉네임)"),
                                fieldWithPath("birthday").type(JsonFieldType.STRING).description("유저 생년월일"),
                                fieldWithPath("profileImageLink").type(JsonFieldType.STRING).description("프로필 사진 링크"),
                                fieldWithPath("userRegions").type(JsonFieldType.ARRAY).description("유저 관심지역들"),
                                fieldWithPath("userRegions[].seq").type(JsonFieldType.NUMBER).description("지역 시퀀스"),
                                fieldWithPath("userRegions[].priority").type(JsonFieldType.NUMBER).description("지역 관심 우선순위"),
                                fieldWithPath("userInterests").type(JsonFieldType.ARRAY).description("유저 관심사"),
                                fieldWithPath("userInterests[].seq").type(JsonFieldType.NUMBER).description("관심사 시퀀스"),
                                fieldWithPath("userInterests[].priority").type(JsonFieldType.NUMBER).description("유저 관심사 우선순위")
                        ),
                        responseFields(
                                *commonResponseField()
                        )
                ))
    }

    @Test
    @WithMockUser(username = "eric", authorities = [Role.MEMBER])
    fun `유저 지역 조회`() {

        // given
        val region1 = Region(superRegion = null, name = "성남시", superRegionRoot = "경기도/성남시", level = 2, subRegions = listOf())
        val region2 = Region(superRegion = null, name = "수원시", superRegionRoot = "경기도/수원시", level = 2, subRegions = listOf())
        region1.seq = 1001L
        region2.seq = 1002L

        val regionWithPriorityDto1 = RegionWithPriorityDto(
                region = SimpleRegionDto(region1)
                , priority = 1L)

        val regionWithPriorityDto2 = RegionWithPriorityDto(
                region = SimpleRegionDto(region2)
                , priority = 2L)

        val user = User("eric")
        user.seq = 1L

        `when`(userRegionService.findUserRegionList(anyObject())).thenReturn(UserRegionDto(
                user, listOf(
                regionWithPriorityDto1,
                regionWithPriorityDto2
        )
        ))

        val result = this.mockMvc.perform(
                get("/users/regions")
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIiwidXNlcklkIjoiMTQ1MTAwMTY0OSIsInN1YiI6IjE0NTEwMDE2NDkiLCJpYXQiOjE1OTc1NDY1MjYsImV4cCI6MTYyOTA4MjUyNn0.1dc1DK7W2iYOXu6BOlrHAbpKnlMkz4o7c7eFtGOWy5M")
                        .characterEncoding("utf-8")
        ).andDo(print())

        result.andExpect(status().isOk)
                .andDo(document("userRegions", getDocumentRequest(), getDocumentResponse(),
                        responseFields(
                                *commonResponseField(),
                                fieldWithPath("data.userRegions").type(JsonFieldType.ARRAY).description("유저 지역들"),
                                fieldWithPath("data.userRegions[].region").type(JsonFieldType.OBJECT).description("유저 지역"),
                                fieldWithPath("data.userRegions[].region.seq").type(JsonFieldType.NUMBER).description("지역 시퀀스"),
                                fieldWithPath("data.userRegions[].region.name").type(JsonFieldType.STRING).description("지역 이름"),
                                fieldWithPath("data.userRegions[].region.superRegionRoot").type(JsonFieldType.STRING).description("지역 루트"),
                                fieldWithPath("data.userRegions[].region.level").type(JsonFieldType.NUMBER).description("지역 단계 레벨"),
                                fieldWithPath("data.userRegions[].priority").type(JsonFieldType.NUMBER).description("유저 지역 우선순위"),
                                fieldWithPath("data.userSeq").type(JsonFieldType.NUMBER).description("유저 시퀀스"),
                                fieldWithPath("data.userId").type(JsonFieldType.STRING).description("유저 아이디")
                        )
                ))
    }

    @Test
    @WithMockUser(username = "eric", authorities = [Role.NONE, Role.MEMBER])
    fun `카카오 유저 정보 조회`() {

        `when`(userService.getKakaoUserInfo(anyObject())).thenReturn(KakaoUserInfo(
                id = "123123",
                properties = KakaoUserProperties(
                        nickname = "정준_ERIC",
                        profile_image = "http://k.kakaocdn.net/dn/A1cab/btqGe6M1iji/MJsLWEwrqX72PLOtCxnCkk/img_640x640.jpg",
                        thumbnail_image = "http://k.kakaocdn.net/dn/A1cab/btqGe6M1iji/MJsLWEwrqX72PLOtCxnCkk/img_110x110.jpg"),
                kakao_account = KakaoUserAccount(
                        profile_needs_agreement = false,
                        profile = KakaoUserProfile(
                                nickname = "정준_ERIC",
                                profile_image_url = "http://k.kakaocdn.net/dn/A1cab/btqGe6M1iji/MJsLWEwrqX72PLOtCxnCkk/img_640x640.jpg",
                                thumbnail_image_url = "http://k.kakaocdn.net/dn/A1cab/btqGe6M1iji/MJsLWEwrqX72PLOtCxnCkk/img_110x110.jpg"
                        ),
                        hasGender = false,
                        gender_needs_agreement = false
                )
        ))

        val result = this.mockMvc.perform(
                get("/users/kakao-profile")
                        .header("Authorization", "Bearer ACACACACACAXCZCZXCXZ")
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        result.andExpect(status().isOk)
                .andDo(document("kakaoUserProfileInfo", getDocumentRequest(), getDocumentResponse(),
                        responseFields(
                                *commonResponseField(),
                                fieldWithPath("data.id").type(JsonFieldType.STRING).description("카카오 고유 유저 ID값"),
                                fieldWithPath("data.properties").type(JsonFieldType.OBJECT).description("추가 정보"),
                                fieldWithPath("data.properties.nickname").type(JsonFieldType.STRING).description("유저 닉네임(아이디)"),
                                fieldWithPath("data.properties.profile_image").type(JsonFieldType.STRING).description("현재 프로필 이미지"),
                                fieldWithPath("data.properties.thumbnail_image").type(JsonFieldType.STRING).description("현재 프로필 이미지(썸네일)"),
                                fieldWithPath("data.kakao_account").type(JsonFieldType.OBJECT).description("계정 정보"),
                                fieldWithPath("data.kakao_account.profile_needs_agreement").type(JsonFieldType.BOOLEAN).description("사용자의 개인정보 제 3자 동의가 필요한지 여부"),
                                fieldWithPath("data.kakao_account.profile").type(JsonFieldType.OBJECT).description("유저 정보"),
                                fieldWithPath("data.kakao_account.profile.nickname").type(JsonFieldType.STRING).description("유저 닉네임(아이디)"),
                                fieldWithPath("data.kakao_account.profile.thumbnail_image_url").type(JsonFieldType.STRING).description("현재 프로필 이미지(썸네일) 링크"),
                                fieldWithPath("data.kakao_account.profile.profile_image_url").type(JsonFieldType.STRING).description("현재 프로필 이미지 링크"),
                                fieldWithPath("data.kakao_account.hasGender").type(JsonFieldType.BOOLEAN).description("성별 조회 여부"),
                                fieldWithPath("data.kakao_account.gender_needs_agreement").type(JsonFieldType.BOOLEAN).description("성별 조회를 위해 동이가 필요한지 여부")
                        )
                ))
    }

    @Test
    @WithMockUser(authorities = [Role.NONE, Role.MEMBER]) //username = "eric"
    fun `유저 지역 변경`() {

        // given
        val mockUser = User("eric")
        mockUser.seq = 1L
        mockUser.userId = "12313"

        val superRegion= Region(name="서울특별시", superRegionRoot = "서울특별시", level = 2L, superRegion = null, subRegions = listOf())
        superRegion.seq  = 1
        val region1 = Region(name="종로구", superRegionRoot = "서울특별시/종로구", level = 2L, superRegion = superRegion, subRegions = listOf())
        region1.seq = 101L
        val region2 = Region(name="중구", superRegionRoot = "서울특별시/중구", level = 2L, superRegion = superRegion, subRegions = listOf())
        region2.seq = 102L

        val userRegions = listOf(
                RegionWithPriorityDto(region = SimpleRegionDto(region = region1), priority = 1L),
                RegionWithPriorityDto(region = SimpleRegionDto(region = region2), priority = 2L)
        )


        val regionRequest = listOf(RegionRequestDto(seq = 101L, priority = 1L), RegionRequestDto(seq = 102L, priority = 2L))
        val userRegionDto = UserRegionDto(user = mockUser, regions = userRegions)

        `when`(userRegionService.changeUserRegion(anyObject(), anyObject())).thenReturn(userRegionDto)

        // when
        val result = this.mockMvc.perform(
                put("/users/regions")
                        .header("Authorization", "Bearer ACACACACACAXCZCZXCXZ")
                        .content(objectMapper.writeValueAsString(regionRequest))
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())


        // then
        result.andExpect(status().isOk)
                .andDo(document("changeUserRegions", getDocumentRequest(), getDocumentResponse(),
                requestFields(
                        fieldWithPath("[].seq").type(JsonFieldType.NUMBER).description("지역 시퀀스"),
                        fieldWithPath("[].priority").type(JsonFieldType.NUMBER).description("지역 우선순위")
                ),
                responseFields(
                        *commonResponseField(),
                        fieldWithPath("data.userSeq").type(JsonFieldType.NUMBER).description("유저 시퀀스"),
                        fieldWithPath("data.userId").type(JsonFieldType.STRING).description("유저 아이디"),
                        fieldWithPath("data.userRegions").type(JsonFieldType.ARRAY).description("유저의 변경 후 지역들"),
                        fieldWithPath("data.userRegions[].region").type(JsonFieldType.OBJECT).description("변경된 유저 지역 정보"),
                        fieldWithPath("data.userRegions[].region.seq").type(JsonFieldType.NUMBER).description("지역 시퀀스"),
                        fieldWithPath("data.userRegions[].region.name").type(JsonFieldType.STRING).description("지역 이름"),
                        fieldWithPath("data.userRegions[].region.superRegionRoot").type(JsonFieldType.STRING).description("지역 루트(최상위부터)"),
                        fieldWithPath("data.userRegions[].region.level").type(JsonFieldType.NUMBER).description("지역 레벨"),
                        fieldWithPath("data.userRegions[].priority").type(JsonFieldType.NUMBER).description("유저가 선택한 지역 우선순위")
                )
            )
        )
    }

    @Test
    @WithMockUser(authorities = [Role.NONE, Role.MEMBER]) //username = "sight"
    fun `유저 정보 조회`() {

        // given
        val mockUser = User("sight")
        mockUser.seq = 1L
        mockUser.userId   = "12313"
        mockUser.birthday = LocalDate.parse("1995-12-12")
        mockUser.profileImageLink = "https://cdn.kakao/sight-profile-img.gif"

        // given - userInfoRegion Data set
        val superRegion= Region(name="서울특별시", superRegionRoot = "서울특별시", level = 2L, superRegion = null, subRegions = listOf())
        val region1    = Region(name="종로구", superRegionRoot = "서울특별시/종로구", level = 2L, superRegion = superRegion, subRegions = listOf())
        val region2    = Region(name="중구", superRegionRoot = "서울특별시/중구", level = 2L, superRegion = superRegion, subRegions = listOf())

        superRegion.seq  = 1
        region1.seq = 101L
        region2.seq = 102L

        val userInfoRegions: List<UserInfoRegionDto> = listOf(
                UserInfoRegionDto(SimpleRegionDto(region1), priority = 1L),
                UserInfoRegionDto(SimpleRegionDto(region2), priority = 2L)
        )

        // given - userInfoInterests Data set
        val interestGroup1 = InterestGroup("여행", emptyList())
        val interest1      = Interest("국내여행", interestGroup1)
        val userInterest1 = UserInterest(mockUser, interest1, priority = 1L)
        interestGroup1.seq = 1
        interest1.seq      = 2
        userInterest1.seq  = 3

        val interestGroup2 = InterestGroup("여행", emptyList())
        val interest2      = Interest("국내여행", interestGroup2)
        val userInterest2 = UserInterest(mockUser, interest2, priority = 1L)
        interestGroup2.seq = 4
        interest2.seq      = 5
        userInterest2.seq  = 6

        val userInfoInterests: List<UserInfoInterestDto> = listOf(
                UserInfoInterestDto(userInterest1),
                UserInfoInterestDto(userInterest2)
        )

        // given - result
        val giverResult: UserInfoDto = UserInfoDto(mockUser, userInfoRegions, userInfoInterests)
        `when`(userInfoService.getUserInfo(anyObject())).thenReturn(giverResult)

        // when
        val result = this.mockMvc.perform(
                get("/users/profile")
                        .header("Authorization", "Bearer ACACACACACAXCZCZXCXZ")
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())


        // then
        result.andExpect(status().isOk)
                .andDo(document("user-info", getDocumentRequest(), getDocumentResponse(),
                        responseFields(
                                *commonResponseField(),
                                fieldWithPath("data.userName").type(JsonFieldType.STRING).description("유저 이름"),
                                fieldWithPath("data.birthday").type(JsonFieldType.STRING).description("유저 생년월일"),
                                fieldWithPath("data.profileImageLink").type(JsonFieldType.STRING).description("유저 카카오 프로필 이미지 링크"),
                                fieldWithPath("data.userRegions[].region").type(JsonFieldType.OBJECT).description("유저 지역"),
                                fieldWithPath("data.userRegions[].region.seq").type(JsonFieldType.NUMBER).description("유저 지역 seq"),
                                fieldWithPath("data.userRegions[].region.name").type(JsonFieldType.STRING).description("유저 지역 이름"),
                                fieldWithPath("data.userRegions[].region.superRegionRoot").type(JsonFieldType.STRING).description("유저 지역 루트(최상위부터)"),
                                fieldWithPath("data.userRegions[].region.level").type(JsonFieldType.NUMBER).description("유저 지역레벨"),
                                fieldWithPath("data.userRegions[].priority").type(JsonFieldType.NUMBER).description("유저 지역 우선순위"),
                                fieldWithPath("data.userInterests[].interest").type(JsonFieldType.OBJECT).description("유저 관심사"),
                                fieldWithPath("data.userInterests[].interest.seq").type(JsonFieldType.NUMBER).description("관심사 seq"),
                                fieldWithPath("data.userInterests[].interest.name").type(JsonFieldType.STRING).description("관심사 명"),
                                fieldWithPath("data.userInterests[].interest.interestGroup.seq").type(JsonFieldType.NUMBER).description("관심사 그룹 seq"),
                                fieldWithPath("data.userInterests[].interest.interestGroup.name").type(JsonFieldType.STRING).description("관심사 그룹 명"),
                                fieldWithPath("data.userInterests[].priority").type(JsonFieldType.NUMBER).description("유저 관심사 우선순위")
                        )
                    )
                )
    }
}