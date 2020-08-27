package com.taskforce.superinvention.document.club

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.interest.interest.InterestDto
import com.taskforce.superinvention.app.domain.interest.interestGroup.InterestGroupDto
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.ClubAddRequestDto
import com.taskforce.superinvention.config.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.ApiDocumentationTest
import com.taskforce.superinvention.config.MockitoHelper
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.mockito.Mockito.`when`
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.test.web.servlet.ResultActions
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.security.crypto.codec.Utf8
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class ClubDocumentation: ApiDocumentationTest() {

    @Test
    @WithMockUser
    fun `모임 생성`() {
        val clubAddRequestDto = ClubAddRequestDto(
                name = "땔감 스터디"
                , description = "땔깜중에서도 고오급 땔깜이 되기 위해 노력하는 스터디"
                , maximumNumber = 5L)

        val result = mockMvc.perform(
                post("/clubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .characterEncoding("UTF-8")
                        .content(objectMapper.writeValueAsString(clubAddRequestDto))
        ).andDo(print())

        result.andExpect(status().isOk)
                .andDo(
                    document("addClub", getDocumentRequest(), getDocumentResponse(),
                        requestFields(
                                fieldWithPath("name").type(JsonFieldType.STRING).description("모임명"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("모임 설명"),
                                fieldWithPath("maximumNumber").type(JsonFieldType.NUMBER).description("모임 최대 인원 수(변경 가능)")
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
                100L
        ))

        val result = mockMvc.perform(
                post("/clubs/232/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdXRoIjoiW1VTRVJdIi")
                        .characterEncoding("UTF-8")
        ).andDo(print())

        result.andExpect(status().isOk)
                .andDo(
                        document("addClubUser", getDocumentRequest(), getDocumentResponse())
                )


    }
}