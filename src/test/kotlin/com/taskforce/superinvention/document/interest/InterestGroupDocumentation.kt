package com.taskforce.superinvention.document.interest

import com.taskforce.superinvention.app.domain.interest.interest.InterestDto
import com.taskforce.superinvention.app.domain.interest.interestGroup.InterestGroupDto
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonResponseField
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.test.ApiDocumentationTest
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.test.web.servlet.ResultActions
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class InterestGroupDocumentation: ApiDocumentationTest() {

    @Test
    fun `관심사 조회 기능`() {

        // given
        val groupList = mutableListOf<InterestGroupDto>()
        groupList.add(InterestGroupDto(
            groupSeq = 1,
            name = "아웃도어/여행",
            interestList = listOf (
                    InterestDto(1, "해외여행"),
                    InterestDto(2, "국내여행"),
                    InterestDto(3, "당일치기"))
        ))

        given(interestGroupService.getInterestList()).willReturn(groupList)

        // when
        val result: ResultActions = this.mockMvc.perform(
                get("/interestGroup/all")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
               .andDo( document("interest-group-all", getDocumentRequest(), getDocumentResponse(),
                        responseFields(
                                *commonResponseField(),
                                fieldWithPath("data.[].interestList").type(JsonFieldType.ARRAY).description("지역 "),
                                fieldWithPath("data.[].interestList[].seq").type(JsonFieldType.NUMBER).description("관심 pk"),
                                fieldWithPath("data.[].interestList[].name").type(JsonFieldType.STRING).description("세부 관심 pk"),
                                fieldWithPath("data.[].name").type(JsonFieldType.STRING).description("관심 그룹"),
                                fieldWithPath("data.[].groupSeq").type(JsonFieldType.NUMBER).description("관심 그룹 pk")
                        )
               ))
    }
}