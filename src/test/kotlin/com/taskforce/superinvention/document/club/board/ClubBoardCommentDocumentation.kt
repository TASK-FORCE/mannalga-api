package com.taskforce.superinvention.document.club.board

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.app.domain.club.board.comment.ClubBoardComment
import com.taskforce.superinvention.app.domain.club.board.comment.ClubBoardCommentService
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.controller.club.board.ClubBoardCommentController
import com.taskforce.superinvention.app.web.dto.club.board.comment.ClubBoardCommentListDto
import com.taskforce.superinvention.app.web.dto.club.board.comment.ClubBoardCommentRegisterDto
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.config.MockitoHelper
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonPageQueryParam
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonResponseField
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.pageFieldDescriptor
import com.taskforce.superinvention.config.test.ApiDocumentationTestV2
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
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

@WebMvcTest(ClubBoardCommentController::class)
class ClubBoardCommentDocumentation: ApiDocumentationTestV2() {

    @MockBean
    private lateinit var clubBoardCommentService: ClubBoardCommentService

    lateinit var club: Club
    lateinit var user: User
    lateinit var clubUser : ClubUser
    lateinit var clubBoard: ClubBoard
    lateinit var clubBoardComment: ClubBoardComment
    lateinit var clubBoardCommentChild: ClubBoardComment

    @BeforeEach
    fun setup() {

        club = Club(
                name = "테스트 모임",
                description   = "",
                maximumNumber = 10,
                mainImageUrl  = ""
        ).apply {
            seq = 88
        }

        user = User ("12345").apply {
            userName = "sight"
            seq = 2
        }

        clubUser = ClubUser(club, user, isLiked = true).apply {
            seq = 110
        }

        clubBoard = ClubBoard(
            title    = "test-title",
            content  = "test-content",
            club     = club,
            clubUser = clubUser,
            category   = ClubBoard.Category.NORMAL
        ).apply { seq = 100 }

        clubBoardComment = ClubBoardComment(
                content = "모임",
                clubUser  = clubUser,
                clubBoard = clubBoard,
                depth = 1
        ).apply {
            seq           = 111
            subCommentCnt = 3
            totalSubCommentCnt = 10
        }

        clubBoardCommentChild = ClubBoardComment(
            content = "모임",
            clubUser  = clubUser,
            clubBoard = clubBoard,
            depth   = 2,
            parent  = clubBoardComment
        ).apply {
            seq           = 119
            subCommentCnt = 0
            totalSubCommentCnt = 0
        }
    }

    @Test
    @WithMockUser(username = "sight", authorities = [Role.CLUB_MEMBER])
    fun `모임 게시판 댓글 목록 조회 - 루트`() {

        // given
        val pageable: Pageable = PageRequest.of(0, 20)
        val clubBoardCommentList: List<ClubBoardCommentListDto> = listOf(
            ClubBoardCommentListDto(clubBoardComment)
        )

        // when
        `when`(clubBoardCommentService.getCommentList(MockitoHelper.anyObject(), eq(pageable), eq(clubBoard.seq!!)))
                .thenReturn(PageDto
                    (PageImpl(clubBoardCommentList, pageable, clubBoardCommentList.size.toLong())))

        val result: ResultActions = this.mockMvc.perform(
                get("/club/{clubSeq}/board/{clubBoardSeq}/comment", club.seq, clubBoard.seq)
                        .queryParam("page", "${pageable.pageNumber}")
                        .queryParam("size", "${pageable.pageSize}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
                .andDo(document("club-board-comment-select", getDocumentRequest(), getDocumentResponse(),
                        requestParameters(
                                *commonPageQueryParam()
                        ),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스"),
                                parameterWithName("clubBoardSeq").description("모임 엘범 시퀀스")
                        ),
                        responseFields(
                                *commonResponseField(),
                                *pageFieldDescriptor(),
                                fieldWithPath("data.content[].commentSeq").type(JsonFieldType.NUMBER).description("댓글 seq"),
                                fieldWithPath("data.content[].writer").type(JsonFieldType.STRING).description("글쓴이 이름"),
                                fieldWithPath("data.content[].writeClubUserSeq").type(JsonFieldType.NUMBER).description("글쓴이  clubUserSeq"),
                                fieldWithPath("data.content[].writerSeq").type(JsonFieldType.NUMBER).description("글쓴이 userSeq"),
                                fieldWithPath("data.content[].registerTime").type(JsonFieldType.STRING).description("등록 시간"),
                                fieldWithPath("data.content[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                                fieldWithPath("data.content[].imgUrl").type(JsonFieldType.STRING).description("댓글 작성자 프로필 url"),
                                fieldWithPath("data.content[].isWrittenByMe").type(JsonFieldType.BOOLEAN).description("조회시, 내가 쓴 글인지 여부"),
                                fieldWithPath("data.content[].depth").type(JsonFieldType.NUMBER).description("현재 댓글 뎁스"),
                                fieldWithPath("data.content[].childCommentCnt").type(JsonFieldType.NUMBER).description("하위 댓글 개수"),
                                fieldWithPath("data.content[].onlyDirectChildCnt").type(JsonFieldType.BOOLEAN).description("childCommentCnt가 하위뎁스 하나만인지 전체인지")
                        )
                ))
    }

    @Test
    @WithMockUser(username = "sight", authorities = [Role.CLUB_MEMBER])
    fun `모임 게시판 댓글 목록 조회 - 하위 댓글`() {

        // given
        val clubBoardCommentList: List<ClubBoardCommentListDto> = listOf(
            ClubBoardCommentListDto(clubBoardComment)
        )

        // when
        `when`(clubBoardCommentService.getChildCommentList(MockitoHelper.anyObject(), eq(clubBoardComment.seq!!), anyLong()))
            .thenReturn(clubBoardCommentList)

        val result: ResultActions = this.mockMvc.perform(
            get("/club/{clubSeq}/board/{clubBoardSeq}/comment/{clubBoardCommentSeq}", club.seq, clubBoard.seq, clubBoardComment.seq)
                .queryParam("depthLimit", "3")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
            .andDo(document("club-board-comment-select-sub", getDocumentRequest(), getDocumentResponse(),
                requestParameters(
                    parameterWithName("depthLimit").description("하위 뎁스 조회 제한")
                ),
                pathParameters(
                    parameterWithName("clubSeq").description("모임 시퀀스"),
                    parameterWithName("clubBoardSeq").description("모임 엘범 시퀀스"),
                    parameterWithName("clubBoardCommentSeq").description("부모 게시판 댓글 시퀀스")
                ),
                responseFields(
                    *commonResponseField(),
                    fieldWithPath("data[].commentSeq").type(JsonFieldType.NUMBER).description("댓글 seq"),
                    fieldWithPath("data[].writer").type(JsonFieldType.STRING).description("글쓴이 이름"),
                    fieldWithPath("data[].writeClubUserSeq").type(JsonFieldType.NUMBER).description("글쓴이  clubUserSeq"),
                    fieldWithPath("data[].writerSeq").type(JsonFieldType.NUMBER).description("글쓴이 userSeq"),
                    fieldWithPath("data[].registerTime").type(JsonFieldType.STRING).description("등록 시간"),
                    fieldWithPath("data[].content").type(JsonFieldType.STRING).description("댓글 내용"),
                    fieldWithPath("data[].imgUrl").type(JsonFieldType.STRING).description("댓글 작성자 프로필 url"),
                    fieldWithPath("data[].isWrittenByMe").type(JsonFieldType.BOOLEAN).description("조회시, 내가 쓴 글인지 여부"),
                    fieldWithPath("data[].depth").type(JsonFieldType.NUMBER).description("현재 댓글 뎁스"),
                    fieldWithPath("data[].childCommentCnt").type(JsonFieldType.NUMBER).description("하위 댓글 개수"),
                    fieldWithPath("data[].onlyDirectChildCnt").type(JsonFieldType.BOOLEAN).description("childCommentCnt가 하위뎁스 하나만인지 전체인지")
                )
            ))
    }

    @Test
    @WithMockUser(username = "sight", authorities = [Role.CLUB_MEMBER])
    fun `모임 게시판 댓글 등록`() {

        // given
        val body = ClubBoardCommentRegisterDto(content = "예시 댓글")

        // when
        `when`(clubBoardCommentService.registerComment(
                clubSeq      = club.seq!!,
                clubBoardSeq = clubBoard.seq!!,
                user         = user,
                body         = body,
            parentCommentSeq = null
        )).then { Unit }

        val result: ResultActions = this.mockMvc.perform(
                post("/club/{clubSeq}/board/{clubBoardSeq}/comment", club.seq, clubBoard.seq)
                        .queryParam("parentCommentSeq", clubBoardComment.seq!!.toString())
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isCreated)
                .andDo(document("club-board-comment-register", getDocumentRequest(), getDocumentResponse(),
                        requestParameters(
                            parameterWithName("parentCommentSeq").description("[optional] 모임 게시판 부모댓글 시퀀스, 없을 경우 루트"),
                        ),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스"),
                                parameterWithName("clubBoardSeq").description("모임 게시판 시퀀스"),
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용")
                        ),
                        responseFields(
                                *commonResponseField()
                        )
                ))
    }

    @Test
    @WithMockUser(username = "sight", authorities = [Role.CLUB_MEMBER])
    fun `모임 게시판 댓글 수정`() {

        // given
        val body = ClubBoardCommentRegisterDto(content = "수정된 댓글 내용")

        // when
        `when`(clubBoardCommentService.editComment(
                clubSeq      = club.seq!!,
                clubBoardSeq = clubBoard.seq!!,
                clubBoardCommentSeq = clubBoardComment.seq!!,
                user         = user,
                body         = body
        )).then { Unit }

        val result: ResultActions = this.mockMvc.perform(
                patch("/club/{clubSeq}/board/{clubBoardSeq}/{clubBoardCommentSeq}", club.seq, clubBoard.seq, clubBoardComment.seq)
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
                .andDo(document("club-board-comment-edit", getDocumentRequest(), getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스"),
                                parameterWithName("clubBoardSeq").description("모임 엘범 시퀀스"),
                                parameterWithName("clubBoardCommentSeq").description("모임 엘범 댓글 시퀀스")
                        ),
                        requestFields(
                                fieldWithPath("content").type(JsonFieldType.STRING).description("댓글 내용")
                        ),
                        responseFields(
                                *commonResponseField()
                        )
                ))
    }

    @Test
    @WithMockUser(username = "sight", authorities = [Role.CLUB_MEMBER])
    fun `모임 게시판 댓글 삭제`() {

        // when
        `when`(clubBoardCommentService.removeComment(
                clubSeq      = club.seq!!,
                clubBoardSeq = clubBoard.seq!!,
                clubBoardCommentSeq = clubBoardComment.seq!!,
                user         = user
        )).then { Unit }

        val result: ResultActions = this.mockMvc.perform(
                delete("/club/{clubSeq}/board/{clubBoardSeq}/{clubBoardCommentSeq}", club.seq, clubBoard.seq, clubBoardComment.seq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
                .andDo(document("club-board-comment-remove", getDocumentRequest(), getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스"),
                                parameterWithName("clubBoardSeq").description("모임 엘범 시퀀스"),
                                parameterWithName("clubBoardCommentSeq").description("모임 엘범 댓글 시퀀스")
                        ),
                        responseFields(
                                *commonResponseField()
                        )
                ))
    }
}
