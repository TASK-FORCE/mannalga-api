package com.taskforce.superinvention.document.region

import com.taskforce.superinvention.app.domain.region.Region
import com.taskforce.superinvention.app.web.dto.region.of
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonResponseField
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

class RegionDocumentation: ApiDocumentationTest() {

    @Test
    fun `Region 조회 기능`() {

        // given
        val region = Region(
                level = 1,
                name = "서울특별시",
                superRegionRoot = "서울특별시",
                subRegions = emptyList(),
                superRegion = null
        )
        region.seq=1

        given(regionService.findAllRegionDtoList())
                .willReturn(listOf(of(region, 1)))

        // when
        val result: ResultActions = this.mockMvc.perform(
                get("/regions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
               .andDo( document("region-all", getDocumentRequest(), getDocumentResponse(),
                        responseFields(
                                *commonResponseField(),
                                fieldWithPath("data.[].seq").type(JsonFieldType.NUMBER).description("시퀀스"),
                                fieldWithPath("data.[].name").type(JsonFieldType.STRING).description("지역 "),
                                fieldWithPath("data.[].superRegionRoot").type(JsonFieldType.STRING).description("상위 지역 명"),
                                fieldWithPath("data.[].level").type(JsonFieldType.NUMBER).description("지역 레벨"),
                                fieldWithPath("data.[].subRegions").type(JsonFieldType.ARRAY).description("하위 지역")
                        )
               ))
    }
}