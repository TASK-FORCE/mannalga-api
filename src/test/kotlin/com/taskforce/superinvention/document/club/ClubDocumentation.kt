package com.taskforce.superinvention.document.club

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.config.ApiDocumentationTest
import java.time.LocalDateTime

import com.taskforce.superinvention.app.domain.state.State
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.UserDetailsService
import com.taskforce.superinvention.app.model.AppToken
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.kakao.*
import com.taskforce.superinvention.app.web.dto.state.*
import com.taskforce.superinvention.config.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.MockitoHelper
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.BDDMockito.*
import org.springframework.context.annotation.Profile
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.context.support.WithUserDetails
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.lang.IllegalArgumentException
import java.time.LocalDate

class ClubDocumentation: ApiDocumentationTest() {

    @Test
    fun `State 조회 기능`() {

        // given
        var club: Club = Club(
                name ="네카라스터디",
                description = "네카라에 가는 모임",
                maximumNumber = 50L
        )
        club.seq = 9999

        `when`(clubService.getClubBySeq(9999)).thenReturn(club)

        // when
        val result: ResultActions = this.mockMvc.perform(
                get("/clubs/9999")
                        .characterEncoding("utf-8")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
                .andDo(document("select-club", getDocumentRequest(), getDocumentResponse(),
                        
                        responseFields(
                                fieldWithPath("seq").type(JsonFieldType.NUMBER).description("모임의 고유 시퀀스"),
                                fieldWithPath("name").type(JsonFieldType.STRING).description("모임의 이름"),
                                fieldWithPath("description").type(JsonFieldType.STRING).description("모임에 대한 설명"),
                                fieldWithPath("maximumNumber").type(JsonFieldType.NUMBER).description("모임의 최대 인원")
                        )
                
                ))
    }
}