package com.taskforce.superinvention.document.user

import com.taskforce.superinvention.app.model.AppToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoToken
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.kakao.KakaoUserRegistRequest
import com.taskforce.superinvention.app.web.dto.state.StateRequestDto
import com.taskforce.superinvention.config.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.ApiDocumentationTest
import com.taskforce.superinvention.config.MockitoHelper
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
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
                isFirst = true,
                appToken = "xxxxxxxxxx"
        )

        `when`(userService.publishAppToken(MockitoHelper.anyObject())).thenReturn(appToken)

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
                                fieldWithPath("isFirst").type(JsonFieldType.BOOLEAN).description("최초 회원가입 여부"),
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
}