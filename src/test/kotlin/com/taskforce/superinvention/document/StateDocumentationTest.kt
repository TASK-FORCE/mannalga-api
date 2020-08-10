package com.taskforce.superinvention.document

import com.taskforce.superinvention.ApiDocumentationTest
import com.taskforce.superinvention.app.domain.state.State
import com.taskforce.superinvention.app.domain.state.StateDto
import com.taskforce.superinvention.app.domain.state.StateService
import com.taskforce.superinvention.testUtil.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.testUtil.ApiDocumentUtil.getDocumentResponse
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.test.web.servlet.ResultActions
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

class StateDocumentationTest: ApiDocumentationTest() {

    @MockBean
    lateinit var stateService: StateService

    @Test
    fun states() {

        // given
        val state = State(
                level = 1,
                name = "name",
                superStateRoot = "서울특별시",
                subStates = emptyList(),
                superState = null
        )

        given(stateService.findAllStateDtoList())
                .willReturn(listOf(StateDto(state)))

        // when
        val result: ResultActions = this.mockMvc.perform(
                RestDocumentationRequestBuilders.get("/states")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
        ).andDo(MockMvcResultHandlers.print())

        // then
        result.andExpect(status().isOk)
               .andDo( document("states-all", getDocumentRequest(), getDocumentResponse(),
                        responseFields(
                                fieldWithPath("[].name").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("[].superStateRoot").type(JsonFieldType.STRING).description("이름"),
                                fieldWithPath("[].level").type(JsonFieldType.NUMBER).description("이름"),
                                fieldWithPath("[].subStates").type(JsonFieldType.ARRAY).description("이름")
                        )
               ))
    }
}