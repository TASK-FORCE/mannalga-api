package com.taskforce.superinvention.document.club

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.app.domain.club.board.ClubBoardRepository
import com.taskforce.superinvention.app.domain.club.board.ClubBoardService
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardBody
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonResponseField
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.pageFieldDescriptor
import com.taskforce.superinvention.config.test.ApiDocumentationTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

class ClubBoardDocumentation: ApiDocumentationTest() {

    @MockBean
    lateinit var clubGroupService: ClubBoardService

    @MockBean
    lateinit var clubBoardRepository: ClubBoardRepository

    @MockBean
    lateinit var clubUserRepository: ClubUserRepository

    lateinit var club: Club
    lateinit var user: User
    lateinit var clubUser : ClubUser
    lateinit var clubBoard: ClubBoard

    @BeforeEach
    fun setup() {
        club = Club(
                name = "테스트 클럽",
                description   = "",
                maximumNumber = 10,
                mainImageUrl  = ""
        )
        club.seq = 88;

        user = User ("12345")
        user.seq = 2

        clubUser = ClubUser(club, user)

        clubBoard = ClubBoard(
                title   = "test-title",
                content = "test-content",
                club = club,
                clubUser = clubUser,
                deleteFlag = false,
                notificationFlag = false,
                topFixedFlag = false
        )
        clubBoard.seq = 300
    }


    @Test
    fun `모임 게시판 글 작성`() {

        // given
        val postBody = ClubBoardBody(
                title   = "test-title",
                content = "test-content"
        )

        // when
        `when`(clubUserRepository.findByClubSeqAndUser(club.seq!!, user)).thenReturn(clubUser)
        `when`(clubBoardRepository.save(clubBoard)).then {Unit}

        val result: ResultActions = this.mockMvc.perform(
                post("/clubs/{clubSeq}/boards", club.seq)
                        .header("Authorization", "Bearer xxxxxxxxxxx")
                        .content(objectMapper.writeValueAsString(postBody))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isCreated)
                .andDo(document("register-club-board", getDocumentRequest(), getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                        ),
                        responseFields(
                                *commonResponseField()
                        )
                ))
    }

    @Test
    fun `모임 게시판 글 조회`() {

        // given
        // when
        `when`(clubUserRepository.findByClubSeqAndUser(club.seq!!, user)).thenReturn(clubUser)
        `when`(clubBoardRepository.save(clubBoard)).then {Unit}

        val result: ResultActions = this.mockMvc.perform(
                get("/clubs/{clubSeq}/boards?page=${1}&size=${10}&title=${"제목"}&content=${"글 내용"}", club.seq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isCreated)
                .andDo(document("select-club-board", getDocumentRequest(), getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubSeq").description("[path variable] 모임 시퀀스"),
                                parameterWithName("page").description("페이지"),
                                parameterWithName("size").description("조회 개수"),
                                parameterWithName("title").description("검색 글 제목 - optional"),
                                parameterWithName("content").description("검색 글 내용 - optional")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용")
                        ),
                        responseFields(
                                *commonResponseField(),
                                *pageFieldDescriptor()
                        )
                ))
    }
}