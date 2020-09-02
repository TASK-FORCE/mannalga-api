package com.taskforce.superinvention.document.user

import com.taskforce.superinvention.app.domain.state.State
import com.taskforce.superinvention.app.domain.user.user.User
import com.taskforce.superinvention.app.domain.user.userState.UserStateService
import com.taskforce.superinvention.app.model.AppToken
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.kakao.*
import com.taskforce.superinvention.app.web.dto.state.*
import com.taskforce.superinvention.config.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.ApiDocumentationTest
import com.taskforce.superinvention.config.MockitoHelper
import com.taskforce.superinvention.config.MockitoHelper.anyObject
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
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
                                fieldWithPath("access_token").type(JsonFieldType.STRING).description("oauth 엑세스 토큰"),
                                fieldWithPath("expireds_in").type(JsonFieldType.NUMBER).description("엑세스 토큰 TTL (초)"),
                                fieldWithPath("refresh_token").type(JsonFieldType.STRING).description("oauth 리프래스 토큰"),
                                fieldWithPath("refresh_token_expires_in").type(JsonFieldType.NUMBER).description("리프래스 토큰 TTL (초)")
                        ),
                        responseFields(
                                fieldWithPath("isRegistered").type(JsonFieldType.BOOLEAN).description("최초 회원가입 여부"),
                                fieldWithPath("appToken").type(JsonFieldType.STRING).description("앱 JWT 토큰")
                        )
                ))
    }

    @Test
    @WithMockUser
    fun `유저 등록`() {
        val kakaoUserRegisterDto = KakaoUserRegistRequest(
                userName = "에릭",
                birthday = LocalDate.parse("1995-12-27"),
                profileImageLink = "",
                userStates = listOf<StateRequestDto>(StateRequestDto(seq = 201, priority = 1), StateRequestDto(seq = 202, priority = 2)),
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
        result.andExpect(status().isOk)
                .andDo(document("userRegist", getDocumentRequest(), getDocumentResponse(),
                        requestFields(
                                fieldWithPath("userName").type(JsonFieldType.STRING).description("유저 이름(닉네임)"),
                                fieldWithPath("birthday").type(JsonFieldType.STRING).description("유저 생년월일"),
                                fieldWithPath("profileImageLink").type(JsonFieldType.STRING).description("프로필 사진 링크"),
                                fieldWithPath("userStates").type(JsonFieldType.ARRAY).description("유저 관심지역들"),
                                fieldWithPath("userStates[].seq").type(JsonFieldType.NUMBER).description("지역 시퀀스"),
                                fieldWithPath("userStates[].priority").type(JsonFieldType.NUMBER).description("지역 관심 우선순위"),
                                fieldWithPath("userInterests").type(JsonFieldType.ARRAY).description("유저 관심사"),
                                fieldWithPath("userInterests[].seq").type(JsonFieldType.NUMBER).description("관심사 시퀀스"),
                                fieldWithPath("userInterests[].priority").type(JsonFieldType.NUMBER).description("유저 관심사 우선순위")
                        )
                ))
    }

    @Test
    @WithMockUser(username = "eric", roles = ["USER", "ADMIN"])
    fun `유저 지역 조회`() {
        // given
        val state1 = State(superState = null, name = "성남시", superStateRoot = "경기도/성남시", level = 2, subStates = listOf())
        val state2 = State(superState = null, name = "수원시", superStateRoot = "경기도/수원시", level = 2, subStates = listOf())
        state1.seq = 1001L
        state2.seq = 1002L

        val stateWithPriorityDto1 = StateWithPriorityDto(
                stateDto = SimpleStateDto(state1)
                , priority = 1L)

        val stateWithPriorityDto2 = StateWithPriorityDto(
                stateDto = SimpleStateDto(state2)
                , priority = 2L)

        val user = User("eric")
        user.seq = 1L

        `when`(userStateService.findUserStateList(anyObject())).thenReturn(UserStateDto(
                user, listOf(
                    stateWithPriorityDto1,
                    stateWithPriorityDto2
                )
        ))

        val result = this.mockMvc.perform(
                get("/users/states")
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIiwidXNlcklkIjoiMTQ1MTAwMTY0OSIsInN1YiI6IjE0NTEwMDE2NDkiLCJpYXQiOjE1OTc1NDY1MjYsImV4cCI6MTYyOTA4MjUyNn0.1dc1DK7W2iYOXu6BOlrHAbpKnlMkz4o7c7eFtGOWy5M")
                        .characterEncoding("utf-8")
        ).andDo(print())



        result.andExpect(status().isOk)
                .andDo(document("userStates", getDocumentRequest(), getDocumentResponse(),
                        responseFields(
                                fieldWithPath("userStates").type(JsonFieldType.ARRAY).description("유저 지역들"),
                                fieldWithPath("userStates[].stateDto").type(JsonFieldType.OBJECT).description("유저 지역"),
                                fieldWithPath("userStates[].stateDto.seq").type(JsonFieldType.NUMBER).description("지역 시퀀스"),
                                fieldWithPath("userStates[].stateDto.name").type(JsonFieldType.STRING).description("지역 이름"),
                                fieldWithPath("userStates[].stateDto.superStateRoot").type(JsonFieldType.STRING).description("지역 루트"),
                                fieldWithPath("userStates[].stateDto.level").type(JsonFieldType.NUMBER).description("지역 단계 레벨"),
                                fieldWithPath("userStates[].priority").type(JsonFieldType.NUMBER).description("유저 지역 우선순위"),
                                fieldWithPath("userSeq").type(JsonFieldType.NUMBER).description("유저 시퀀스"),
                                fieldWithPath("userId").type(JsonFieldType.STRING).description("유저 아이디")
                        )
                ))

    }

    @Test
    @WithMockUser(username = "eric")
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
                                fieldWithPath("id").type(JsonFieldType.STRING).description("카카오 고유 유저 ID값"),
                                fieldWithPath("properties").type(JsonFieldType.OBJECT).description("추가 정보"),
                                fieldWithPath("properties.nickname").type(JsonFieldType.STRING).description("유저 닉네임(아이디)"),
                                fieldWithPath("properties.profile_image").type(JsonFieldType.STRING).description("현재 프로필 이미지"),
                                fieldWithPath("properties.thumbnail_image").type(JsonFieldType.STRING).description("현재 프로필 이미지(썸네일)"),
                                fieldWithPath("kakao_account").type(JsonFieldType.OBJECT).description("계정 정보"),
                                fieldWithPath("kakao_account.profile_needs_agreement").type(JsonFieldType.BOOLEAN).description("사용자의 개인정보 제 3자 동의가 필요한지 여부"),
                                fieldWithPath("kakao_account.profile").type(JsonFieldType.OBJECT).description("유저 정보"),
                                fieldWithPath("kakao_account.profile.nickname").type(JsonFieldType.STRING).description("유저 닉네임(아이디)"),
                                fieldWithPath("kakao_account.profile.thumbnail_image_url").type(JsonFieldType.STRING).description("현재 프로필 이미지(썸네일) 링크"),
                                fieldWithPath("kakao_account.profile.profile_image_url").type(JsonFieldType.STRING).description("현재 프로필 이미지 링크"),
                                fieldWithPath("kakao_account.hasGender").type(JsonFieldType.BOOLEAN).description("성별 조회 여부"),
                                fieldWithPath("kakao_account.gender_needs_agreement").type(JsonFieldType.BOOLEAN).description("성별 조회를 위해 동이가 필요한지 여부")
                        )
                ))
    }


    @Test
    @WithMockUser(username = "eric")
    fun `유저 지역 변경`() {
        // given
        val mockUser = User("eric")
        mockUser.seq = 1L
        mockUser.userId = "12313"

        val superState= State(name="서울특별시", superStateRoot = "서울특별시", level = 2L, superState = null, subStates = listOf())
        superState.seq  = 1
        val state1 = State(name="종로구", superStateRoot = "서울특별시/종로구", level = 2L, superState = superState, subStates = listOf())
        state1.seq = 101L
        val state2 = State(name="중구", superStateRoot = "서울특별시/중구", level = 2L, superState = superState, subStates = listOf())
        state2.seq = 102L

        val userStates = listOf(
                StateWithPriorityDto(stateDto = SimpleStateDto(state = state1), priority = 1L),
                StateWithPriorityDto(stateDto = SimpleStateDto(state = state2), priority = 2L)
        )


        val stateRequest = listOf(StateRequestDto(seq = 101L, priority = 1L), StateRequestDto(seq = 102L, priority = 2L))
        val userStateDto = UserStateDto(user = mockUser, states = userStates)

        `when`(userStateService.changeUserState(anyObject(), anyObject())).thenReturn(userStateDto)

        // when
        val result = this.mockMvc.perform(
                put("/users/states")
                        .header("Authorization", "Bearer ACACACACACAXCZCZXCXZ")
                        .content(objectMapper.writeValueAsString(stateRequest))
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())


        // then
        result.andExpect(status().isOk)
                .andDo(document("changeUserStates", getDocumentRequest(), getDocumentResponse(),
                requestFields(
                        fieldWithPath("[].seq").type(JsonFieldType.NUMBER).description("지역 시퀀스"),
                        fieldWithPath("[].priority").type(JsonFieldType.NUMBER).description("지역 우선순위")
                ),
                responseFields(
                    fieldWithPath("userSeq").type(JsonFieldType.NUMBER).description("유저 시퀀스"),
                    fieldWithPath("userId").type(JsonFieldType.STRING).description("유저 아이디"),
                    fieldWithPath("userStates").type(JsonFieldType.ARRAY).description("유저의 변경 후 지역들"),
                    fieldWithPath("userStates[].stateDto").type(JsonFieldType.OBJECT).description("변경된 유저 지역 정보"),
                    fieldWithPath("userStates[].stateDto.seq").type(JsonFieldType.NUMBER).description("지역 시퀀스"),
                    fieldWithPath("userStates[].stateDto.name").type(JsonFieldType.STRING).description("지역 이름"),
                    fieldWithPath("userStates[].stateDto.superStateRoot").type(JsonFieldType.STRING).description("지역 루트(최상위부터)"),
                    fieldWithPath("userStates[].stateDto.level").type(JsonFieldType.NUMBER).description("지역 레벨"),
                    fieldWithPath("userStates[].priority").type(JsonFieldType.NUMBER).description("유저가 선택한 지역 우선순위")
                )))


    }
}