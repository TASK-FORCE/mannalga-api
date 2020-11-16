package com.taskforce.superinvention.document.club.board

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.app.domain.club.board.ClubBoardRepository
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardBody
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardPreviewDto
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardSearchOpt
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import com.taskforce.superinvention.config.MockitoHelper.anyObject
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonPageQueryParam
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonResponseField
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.pageFieldDescriptor
import com.taskforce.superinvention.config.test.ApiDocumentationTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
import org.springframework.boot.test.mock.mockito.MockBean
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

class ClubBoardDocumentation: ApiDocumentationTest() {

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

        clubUser = ClubUser(club, user, isLiked = false)

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
        val s3path = S3Path(
                absolutePath = "https://super-invention-static.s3.ap-northeast-2.amazonaws.com/test/i3.jpg",
                fileName = "i3.jpg",
                filePath = "test/i3.jpg"
        )

        val dummyImgList = listOf(
                s3path, // title
                s3path,
                s3path
        )

        val postBody = ClubBoardBody(
                title   = "글 제목",
                content = "내용",
                isTopFixed   = false,
                isNotifiable = false,
                imgList = dummyImgList
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
                                fieldWithPath("content").type(JsonFieldType.STRING).description("내용"),
                                fieldWithPath("isTopFixed").type(JsonFieldType.BOOLEAN).description("제목"),
                                fieldWithPath("isNotifiable").type(JsonFieldType.BOOLEAN).description("내용"),
                                fieldWithPath("imgList[].absolutePath").type(JsonFieldType.STRING).description("전체 경로 ( 도메인 포함 )"),
                                fieldWithPath("imgList[].filePath").type(JsonFieldType.STRING).description("파일 경로"),
                                fieldWithPath("imgList[].fileName").type(JsonFieldType.STRING).description("파일 명")
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

        val searchOpt = ClubBoardSearchOpt()
        val clubSeq =  88L

        val dummyItem = ClubBoardPreviewDto (
                clubBoardSeq = 0,
                clubUserSeq  = 1,
                title = "글 제목",
                userName = "글 내용",
                createdAt = "yyyy/mm/dd hh:mm:ss",
                titleImgUrl = "제목 이미지 URL - 없으면 공백",
                photoCnt = 3, // 등록된 사진 개수
                topFixedFlag = false,
                notificationFlag = false
        )

        val dummyItemList = listOf(dummyItem, dummyItem,dummyItem)
        val resultItemList =  PageImpl(dummyItemList, pageable, 100)
        given(clubBoardService.getClubBoardList(anyObject(), anyObject(), anyLong())).willReturn(resultItemList)

        //  when
        val result: ResultActions = this.mockMvc.perform(
                get("/clubs/{clubSeq}/boards", clubSeq)
                        .queryParam("page", "$page")
                        .queryParam("size", "$pageSize")
                        .queryParam("title", searchOpt.title)
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
                                parameterWithName("content").description("검색 내용")
                        ),
                        pathParameters(
                                parameterWithName("clubSeq").description("[path variable] 모임 시퀀스")
                        ),
                        responseFields(
                                *commonResponseField(),
                                *pageFieldDescriptor(),
                                fieldWithPath("data.content[].clubBoardSeq").type(JsonFieldType.NUMBER).description("게시글 seq"),
                                fieldWithPath("data.content[].clubUserSeq").type(JsonFieldType.NUMBER).description("클럽 유저 seq"),
                                fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("글 제목"),
                                fieldWithPath("data.content[].userName").type(JsonFieldType.STRING).description("작성자 명"),
                                fieldWithPath("data.content[].createdAt").type(JsonFieldType.STRING).description("최초 작성 날짜"),
                                fieldWithPath("data.content[].titleImgUrl").type(JsonFieldType.STRING).description("제목 이미지 url - 없을 시 공백"),
                                fieldWithPath("data.content[].photoCnt").type(JsonFieldType.NUMBER).description("해당 글 사진 총 개수"),
                                fieldWithPath("data.content[].topFixedFlag").type(JsonFieldType.BOOLEAN).description("상단고정 표시여부"),
                                fieldWithPath("data.content[].notificationFlag").type(JsonFieldType.BOOLEAN).description("알림 여부")
                        )
                ))
    }

    @Test
    @WithMockUser(username = "sight", authorities = [Role.MEMBER])
    fun `모임 게시판 글 목록 삭제`() {

        // given
        val clubSeq =  88L

        //  when
        `when`(clubBoardService.deleteClubBoard(user, clubSeq)).then{ Unit }

        val result: ResultActions = this.mockMvc.perform(
                delete("/clubs/{clubBoardSeq}/boards", clubSeq)
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