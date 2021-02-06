package com.taskforce.superinvention.document.club.board

import com.ninjasquad.springmockk.MockkBean
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.app.domain.club.board.ClubBoardRepository
import com.taskforce.superinvention.app.domain.club.board.ClubBoardService
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.role.ClubUserRole
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.role.RoleGroup
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.controller.club.board.ClubBoardController
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardDto
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardListViewDto
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardRegisterBody
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardSearchOpt
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonPageQueryParam
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonResponseField
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.pageFieldDescriptor
import com.taskforce.superinvention.config.test.ApiDocumentationTestV2
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(ClubBoardController::class)
class ClubBoardDocumentation: ApiDocumentationTestV2() {

    @MockkBean(relaxUnitFun = true)
    lateinit var clubBoardService: ClubBoardService

    @MockkBean(relaxUnitFun = true)
    lateinit var clubBoardRepository: ClubBoardRepository

    @MockkBean(relaxUnitFun = true)
    lateinit var clubUserRepository: ClubUserRepository

    private lateinit var club: Club
    private lateinit var user: User
    private lateinit var clubUser : ClubUser
    private lateinit var clubBoard: ClubBoard

    private val roleGroup  = RoleGroup("USER", "USER_TYPE")
    private val memberRole = Role(Role.RoleName.CLUB_MEMBER, roleGroup,2)

    @BeforeEach
    fun setup() {
        club = Club(
                name = "테스트 클럽",
                description   = "",
                maximumNumber = 10,
                mainImageUrl  = ""
        ).apply { seq = 88 }

        user = User ("12345").apply { seq = 2 }
        clubUser = ClubUser(club, user, isLiked = false)
            .apply {
                seq = 111;
                clubUserRoles = mutableSetOf(ClubUserRole(this, memberRole))
            }

        clubBoard = ClubBoard(
                title    = "test-title",
                content  = "test-content",
                club     = club,
                clubUser = clubUser,
                deleteFlag = false,
                category   = ClubBoard.Category.NORMAL
        ).apply { seq = 300 }
    }

    @Test
    fun `모임 게시판 글 작성`() {

        // given
        val s3path = S3Path(
                absolutePath = "https://super-invention-static.s3.ap-northeast-2.amazonaws.com/test/i3.jpg",
                fileName = "i3.jpg",
                filePath = "test/i3.jpg"
        )

        val imgList = listOf(s3path)
        val postBody = ClubBoardRegisterBody(
            title    = "글 제목",
            content  = "내용",
            imgList  = imgList,
            category = ClubBoard.Category.NORMAL
        )

        // when
        every { clubBoardService.registerClubBoard(any(), any(), any()) } returns clubBoard

        val result: ResultActions = mockMvc.perform(
                post("/clubs/{clubSeq}/board", club.seq)
                        .header("Authorization", "Bearer xxxxxxxxxxx")
                        .characterEncoding("utf-8")
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
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("imgList[].absolutePath").type(JsonFieldType.STRING).description("전체 경로 ( 도메인 포함 )"),
                                fieldWithPath("imgList[].filePath").type(JsonFieldType.STRING).description("파일 경로"),
                                fieldWithPath("imgList[].fileName").type(JsonFieldType.STRING).description("파일 명"),
                                fieldWithPath("category").type(JsonFieldType.STRING).description("글 상태, NORMAL | NOTICE"),
                        ),
                        responseFields(
                                *commonResponseField()
                        )
                ))
    }

    @Test
    fun `모임 게시판 글 목록 조회`() {

        // given
        val page = 1
        val pageSize = 10
        val pageable = PageRequest.of(page, pageSize)

        val searchOpt = ClubBoardSearchOpt(
            title   = "제목",
            content = "내용"
        )
        val clubSeq =  88L

        val dummyItem = ClubBoardListViewDto(clubBoard)

        val dummyItemList = listOf(dummyItem)
        val resultItemList =  PageImpl(dummyItemList, pageable, 100)

        every { clubBoardService.getClubBoardList(any(), any(), any(), any()) }.returns(PageDto(resultItemList))

        //  when
        val result: ResultActions = this.mockMvc.perform(
                get("/clubs/{clubSeq}/board", clubSeq)
                        .queryParam("page", "$page")
                        .queryParam("size", "$pageSize")
                        .queryParam("category", ClubBoard.Category.NORMAL.label)
                        .queryParam("title"  , searchOpt.title)
                        .queryParam("content", searchOpt.content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
                .andDo(document("select-club-board", getDocumentRequest(), getDocumentResponse(),
                        requestParameters(
                                *commonPageQueryParam(),
                                parameterWithName("title").description("검색 글 제목"),
                                parameterWithName("content").description("검색 내용"),
                                parameterWithName("category").description("검색 카테고리 (NORMAL, NOTICE)"),
                        ),
                        pathParameters(
                                parameterWithName("clubSeq").description("[path variable] 모임 시퀀스")
                        ),
                        responseFields(
                                *commonResponseField(),
                                *pageFieldDescriptor(),
                                fieldWithPath("data.content[].boardSeq").type(JsonFieldType.NUMBER).description("게시글 seq"),
                                fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("글 제목"),
                                fieldWithPath("data.content[].simpleContent").type(JsonFieldType.STRING).description("[50자 제한] 게시글"),
                                fieldWithPath("data.content[].mainImageUrl").type(JsonFieldType.STRING).description("글 제목 이미지 URL - 없으면 공백"),
                                fieldWithPath("data.content[].createAt").type(JsonFieldType.STRING).description("작성 시간"),
                                fieldWithPath("data.content[].category").type(JsonFieldType.STRING).description("글 종류"),
                                fieldWithPath("data.content[].likeCnt").type(JsonFieldType.NUMBER).description("좋아요 개수"),
                                fieldWithPath("data.content[].commentCnt").type(JsonFieldType.NUMBER).description("댓글 개수"),
                                fieldWithPath("data.content[].writer").type(JsonFieldType.OBJECT).description("해당 글 사진 총 개수"),
                                fieldWithPath("data.content[].writer.writerUserSeq").type(JsonFieldType.NUMBER).description("해당 글 사진 총 개수"),
                                fieldWithPath("data.content[].writer.writerClubUserSeq").type(JsonFieldType.NUMBER).description("해당 글 사진 총 개수"),
                                fieldWithPath("data.content[].writer.name").type(JsonFieldType.STRING).description("작성자 명"),
                                fieldWithPath("data.content[].writer.imgUrl").type(JsonFieldType.STRING).description("작성자 프로필 URL"),
                                fieldWithPath("data.content[].writer.role[]").type(JsonFieldType.ARRAY).description("작성자 권한")
                        )
                ))
    }

    @Test
    fun `모임 게시판 글 단건 조회`() {

        // given
        every { clubBoardService.getClubBoard(any(), any() ) }.returns(ClubBoardDto(clubBoard))

        //  when
        val result: ResultActions = this.mockMvc.perform(
            get("/clubs/{clubSeq}/board/{clubBoardSeq}", club.seq, clubBoard.seq)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
            .andDo(document("select-club-board", getDocumentRequest(), getDocumentResponse(),
                pathParameters(
                    parameterWithName("clubSeq").description("[path variable] 모임 시퀀스"),
                    parameterWithName("clubBoardSeq").description("[path variable] 모임 게시판 시퀀스")
                ),
                responseFields(
                    *commonResponseField(),
                    fieldWithPath("data.boardSeq").type(JsonFieldType.NUMBER).description("게시글 seq"),
                    fieldWithPath("data.title").type(JsonFieldType.STRING).description("글 제목"),
                    fieldWithPath("data.content").type(JsonFieldType.STRING).description("게시글"),
                    fieldWithPath("data.imageList[]").type(JsonFieldType.ARRAY).description("이미지 목록"),
                    fieldWithPath("data.createdAt").type(JsonFieldType.STRING).description("작성 시간"),
                    fieldWithPath("data.updatedAt").type(JsonFieldType.STRING).description("수정 시간"),
                    fieldWithPath("data.category").type(JsonFieldType.STRING).description("글 종류"),
                    fieldWithPath("data.likeCnt").type(JsonFieldType.NUMBER).description("좋아요 개수"),
                    fieldWithPath("data.commentCnt").type(JsonFieldType.NUMBER).description("댓글 개수"),
                    fieldWithPath("data.writer").type(JsonFieldType.OBJECT).description("해당 글 사진 총 개수"),
                    fieldWithPath("data.writer.writerUserSeq").type(JsonFieldType.NUMBER).description("해당 글 사진 총 개수"),
                    fieldWithPath("data.writer.writerClubUserSeq").type(JsonFieldType.NUMBER).description("해당 글 사진 총 개수"),
                    fieldWithPath("data.writer.name").type(JsonFieldType.STRING).description("작성자 명"),
                    fieldWithPath("data.writer.imgUrl").type(JsonFieldType.STRING).description("작성자 프로필 URL"),
                    fieldWithPath("data.writer.role[]").type(JsonFieldType.ARRAY).description("작성자 권한")
                )
            ))
    }

    @Test
    @WithMockUser(username = "sight", authorities = [Role.MEMBER])
    fun `모임 게시판 글 목록 삭제`() {

        // given
        val clubSeq =  88L

        //  when
        val result: ResultActions = this.mockMvc.perform(
                delete("/clubs/{clubBoardSeq}/board", clubSeq)
                        .header("Authorization", "Bearer xxxxxxxxxxx")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
                .andDo(document("delete-club-board", getDocumentRequest(), getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubBoardSeq").description("[path variable] 모임 게시판 시퀀스")
                        ),
                        responseFields(
                                *commonResponseField()
                        )
                ))
    }
}