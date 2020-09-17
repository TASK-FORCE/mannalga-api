package com.taskforce.superinvention.document.state

import com.taskforce.superinvention.app.domain.state.State
import com.taskforce.superinvention.app.web.dto.state.of
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.test.ApiDocumentationTest
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.test.web.servlet.ResultActions
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class StateDocumentation: ApiDocumentationTest() {

    @Test
    fun `State 조회 기능`() {

        // given
        val state = State(
                level = 1,
                name = "서울특별시",
                superStateRoot = "서울특별시",
                subStates = emptyList(),
                superState = null
        )
        state.seq=1

        given(stateService.findAllStateDtoList())
                .willReturn(listOf(of(state, 1)))

        // when
        val result: ResultActions = this.mockMvc.perform(
                get("/states")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
               .andDo( document("state-all", getDocumentRequest(), getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].seq").type(JsonFieldType.NUMBER).description("시퀀스"),
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("지역 "),
                                fieldWithPath("[].superStateRoot").type(JsonFieldType.STRING).description("상위 지역 명"),
                                fieldWithPath("[].level").type(JsonFieldType.NUMBER).description("지역 레벨"),
                                fieldWithPath("[].subStates").type(JsonFieldType.ARRAY).description("하위 지역")
                        )
               ))
    }
}