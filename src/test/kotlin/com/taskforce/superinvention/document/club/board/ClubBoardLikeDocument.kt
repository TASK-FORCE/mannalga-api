package com.taskforce.superinvention.document.club.board

import com.ninjasquad.springmockk.MockkBean
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.app.domain.club.board.like.ClubBoardLike
import com.taskforce.superinvention.app.domain.club.board.like.ClubBoardLikeService
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.controller.club.board.ClubBoardLikeController
import com.taskforce.superinvention.app.web.dto.club.board.like.ClubBoardLikeDto
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonResponseField
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.test.ApiDocumentationTestV2
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(ClubBoardLikeController::class)
class ClubBoardLikeDocument: ApiDocumentationTestV2() {

    private lateinit var club: Club
    private lateinit var user: User
    private lateinit var clubUser : ClubUser
    private lateinit var clubBoard: ClubBoard
    private lateinit var clubBoardLike: ClubBoardLike

    @MockkBean
    private lateinit var clubBoardLikeService: ClubBoardLikeService

    @BeforeEach
    fun setup() {

        club = Club(
                name = "테스트 모임",
                description   = "",
                maximumNumber = 10,
                mainImageUrl  = ""
        ).apply { seq = 88 }

        user = User ("12345").apply { seq = 2 }
        user.userName = "sight"

        clubUser = ClubUser(club, user, isLiked = true).apply { seq  = 110 }

        clubBoard = ClubBoard(
            title    = "test-title",
            content  = "test-content",
            club     = club,
            clubUser = clubUser,
            category   = ClubBoard.Category.NORMAL
        ).apply { seq = 100 }

        clubBoardLike = ClubBoardLike(
                clubUser  = clubUser,
                clubBoard = clubBoard
        ).apply { seq = 111 }

    }

    @Test
    @WithMockUser(username = "sight")
    fun `모임 게시판 좋아요 등록`() {

        // given
        val clubBoardLikeDto = ClubBoardLikeDto(
            clubBoardSeq = clubBoard.seq!!,
            clubSeq      = club.seq!!,
            likeCnt      = 20
        )

        every {
            clubBoardLikeService.registerClubBoardLike(
                clubSeq = club.seq!!,
                clubBoardSeq = clubBoard.seq!!,
                user = any()
            )
        } returns clubBoardLikeDto

        // when
        val result: ResultActions = this.mockMvc.perform(
                post("/club/{clubSeq}/board/{clubBoardSeq}/like", club.seq, clubBoard.seq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isCreated)
                .andDo(document(
                        "club-board-like-register",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스"),
                                parameterWithName("clubBoardSeq").description("모임 엘범 시퀀스")
                        ),
                        responseFields(
                                *commonResponseField(),
                                fieldWithPath("data.clubSeq").type(JsonFieldType.NUMBER).description("클럽 seq"),
                                fieldWithPath("data.clubBoardSeq").type(JsonFieldType.NUMBER).description("게시판 seq"),
                                fieldWithPath("data.likeCnt").type(JsonFieldType.NUMBER).description("좋아요 개수")
                        )
                ))
    }

    @Test
    fun `모임 게시판 좋아요 삭제`() {
        // given
        val clubBoardLikeDto = ClubBoardLikeDto(
            clubBoardSeq = clubBoard.seq!!,
            clubSeq      = club.seq!!,
            likeCnt      = 20
        )

        every {clubBoardLikeService.removeClubBoardLike(
            clubSeq = club.seq!!,
            clubBoardSeq = clubBoard.seq!!,
            user = any()
        )} returns clubBoardLikeDto

        // when
        val result: ResultActions = this.mockMvc.perform(
            delete("/club/{clubSeq}/board/{clubBoardSeq}/like", club.seq, clubBoard.seq)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
            .andDo(document(
                "club-board-like-remove",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("clubSeq").description("모임 시퀀스"),
                    parameterWithName("clubBoardSeq").description("모임 엘범 시퀀스")
                ),
                responseFields(
                    *commonResponseField(),
                    fieldWithPath("data.clubSeq").type(JsonFieldType.NUMBER).description("클럽 seq"),
                    fieldWithPath("data.clubBoardSeq").type(JsonFieldType.NUMBER).description("게시판 seq"),
                    fieldWithPath("data.likeCnt").type(JsonFieldType.NUMBER).description("좋아요 개수")
                )
            ))
    }
}
