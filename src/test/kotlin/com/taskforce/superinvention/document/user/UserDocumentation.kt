package com.taskforce.superinvention.document.user

import com.taskforce.superinvention.app.domain.user.user.UserService
import com.taskforce.superinvention.app.model.AppToken
import com.taskforce.superinvention.app.web.dto.kakao.KakaoToken
import com.taskforce.superinvention.app.domain.user.user.UserRegisterDto
import com.taskforce.superinvention.config.ApiDocumentationTest
import com.taskforce.superinvention.config.ApiDocumentUtil
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

class UserDocumentation : ApiDocumentationTest() {

    @Test
    fun `유저 최초 로그인 처리`() {
        // path => /saveKakaoToken

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

        given(userService.publishAppToken(kakoToken))
                .willReturn(appToken)

        // when
        val result: ResultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/saveKakaoToken")
                        .requestAttr("kakaoToken", kakoToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        // then
        result.andExpect(MockMvcResultMatchers.status().isOk)
                .andDo(MockMvcRestDocumentation.document("saveKakaoToken", ApiDocumentUtil.getDocumentRequest(), ApiDocumentUtil.getDocumentResponse(),
                        PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("isFirst").type(JsonFieldType.BOOLEAN).description("최초 회원가입 여부"),
                                PayloadDocumentation.fieldWithPath("appToken").type(JsonFieldType.STRING).description("앱 JWT 토큰")
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