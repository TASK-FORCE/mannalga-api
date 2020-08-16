package com.taskforce.superinvention.document.user

import com.taskforce.superinvention.app.model.AppToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoToken
import com.taskforce.superinvention.app.domain.user.UserRegisterDto
import com.taskforce.superinvention.config.ApiDocumentUtil
import com.taskforce.superinvention.config.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.ApiDocumentationTest
import com.taskforce.superinvention.config.MockitoHelper
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.*
import org.mockito.BDDMockito.*
import org.mockito.Mockito
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

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
    fun `유저 등록`() {
        // path => /register

        val postBody = UserRegisterDto(
                profile = "sight",
                selectedLocations = arrayListOf("서울시 관악구", "서울시 성북구"),
                selectedInterests = arrayListOf("")
        )
    }
}