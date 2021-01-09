package com.taskforce.superinvention.document.club.album

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.album.ClubAlbum
import com.taskforce.superinvention.app.domain.club.album.comment.ClubAlbumComment
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.album.comment.ClubAlbumCommentListDto
import com.taskforce.superinvention.app.web.dto.club.album.comment.ClubAlbumCommentRegisterDto
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.config.MockitoHelper
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonPageQueryParam
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonResponseField
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.pageFieldDescriptor
import com.taskforce.superinvention.config.test.ApiDocumentationTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
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

class ClubAlbumCommentDocumentation: ApiDocumentationTest() {

    lateinit var club: Club
    lateinit var user: User
    lateinit var clubUser : ClubUser
    lateinit var clubAlbum: ClubAlbum
    lateinit var clubAlbumComment: ClubAlbumComment
    lateinit var clubAlbumCommentChild: ClubAlbumComment

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

        clubAlbum = ClubAlbum (
                club = club,
                writer = clubUser,
                title       = "모임 사진첩 사진 1",
                img_url     = "이미지 URL",
                file_name   = "파일 이름",
                delete_flag = false
        ).apply {
            seq = 100
        }

        clubAlbumComment = ClubAlbumComment(
                content = "모임",
                clubUser  = clubUser,
                clubAlbum = clubAlbum
        ).apply {
            seq           = 111
            subCommentCnt = 3
            totalSubCommentCnt = 10
        }

        clubAlbumCommentChild = ClubAlbumComment(
            content = "모임",
            clubUser  = clubUser,
            clubAlbum = clubAlbum
        ).apply {
            seq           = 119
            subCommentCnt = 0
            totalSubCommentCnt = 0
            depth   = 2
            parent  = clubAlbumComment
        }

    }

    @Test
    @WithMockUser(username = "sight", authorities = [Role.CLUB_MEMBER])
    fun `모임 사진첩 댓글 목록 조회 - 루트`() {

        // given
        val pageable: Pageable = PageRequest.of(0, 20)
        val clubAlbumCommentList: List<ClubAlbumCommentListDto> = listOf(
                ClubAlbumCommentListDto(clubAlbumComment)
        )

        // when
        `when`(clubAlbumCommentService.getCommentList(MockitoHelper.anyObject(), eq(pageable), eq(clubAlbum.seq!!)))
                .thenReturn(PageDto
                    (PageImpl(clubAlbumCommentList, pageable, clubAlbumCommentList.size.toLong())))

        val result: ResultActions = this.mockMvc.perform(
                get("/club/{clubSeq}/album/{clubAlbumSeq}/comment", club.seq, clubAlbum.seq)
                        .queryParam("page", "${pageable.pageNumber}")
                        .queryParam("size", "${pageable.pageSize}")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
                .andDo(document("club-album-comment-select", getDocumentRequest(), getDocumentResponse(),
                        requestParameters(
                                *commonPageQueryParam()
                        ),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스"),
                                parameterWithName("clubAlbumSeq").description("모임 엘범 시퀀스")
                        ),
                        responseFields(
                                *commonResponseField(),
                                *pageFieldDescriptor(),
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
    fun `모임 사진첩 댓글 목록 조회 - 하위 댓글`() {

        // given
        val clubAlbumCommentList: List<ClubAlbumCommentListDto> = listOf(
            ClubAlbumCommentListDto(clubAlbumCommentChild)
        )

        // when
        `when`(clubAlbumCommentService.getChildCommentList(MockitoHelper.anyObject(), eq(clubAlbumComment.seq!!), anyLong()))
            .thenReturn(clubAlbumCommentList)

        val result: ResultActions = this.mockMvc.perform(
            get("/club/{clubSeq}/album/{clubAlbumSeq}/comment/{clubAlbumCommentSeq}", club.seq, clubAlbum.seq, clubAlbumComment.seq)
                .queryParam("depthLimit", "3")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
            .andDo(document("club-album-comment-select-sub", getDocumentRequest(), getDocumentResponse(),
                requestParameters(
                    parameterWithName("depthLimit").description("하위 뎁스 조회 제한")
                ),
                pathParameters(
                    parameterWithName("clubSeq").description("모임 시퀀스"),
                    parameterWithName("clubAlbumSeq").description("모임 엘범 시퀀스"),
                    parameterWithName("clubAlbumCommentSeq").description("부모 사진첩 댓글 시퀀스")
                ),
                responseFields(
                    *commonResponseField(),
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
    fun `모임 사진첩 댓글 등록`() {

        // given
        val body = ClubAlbumCommentRegisterDto(content = "예시 댓글")

        // when
        `when`(clubAlbumCommentService.registerComment(
                clubSeq      = club.seq!!,
                clubAlbumSeq = clubAlbum.seq!!,
                user         = user,
                body         = body,
            parentCommentSeq = null
        )).then { Unit }

        val result: ResultActions = this.mockMvc.perform(
                post("/club/{clubSeq}/album/{clubAlbumSeq}/comment", club.seq, clubAlbum.seq)
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isCreated)
                .andDo(document("club-album-comment-register", getDocumentRequest(), getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스"),
                                parameterWithName("clubAlbumSeq").description("모임 엘범 시퀀스")
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
    fun `모임 사진첩 댓글 수정`() {

        // given
        val body = ClubAlbumCommentRegisterDto(content = "수정된 댓글 내용")

        // when
        `when`(clubAlbumCommentService.editComment(
                clubSeq      = club.seq!!,
                clubAlbumSeq = clubAlbum.seq!!,
                clubAlbumCommentSeq = clubAlbumComment.seq!!,
                user         = user,
                body         = body
        )).then { Unit }

        val result: ResultActions = this.mockMvc.perform(
                patch("/club/{clubSeq}/album/{clubAlbumSeq}/{clubAlbumCommentSeq}", club.seq, clubAlbum.seq, clubAlbumComment.seq)
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
                .andDo(document("club-album-comment-edit", getDocumentRequest(), getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스"),
                                parameterWithName("clubAlbumSeq").description("모임 엘범 시퀀스"),
                                parameterWithName("clubAlbumCommentSeq").description("모임 엘범 댓글 시퀀스")
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
    fun `모임 사진첩 댓글 삭제`() {

        // when
        `when`(clubAlbumCommentService.removeComment(
                clubSeq      = club.seq!!,
                clubAlbumSeq = clubAlbum.seq!!,
                clubAlbumCommentSeq = clubAlbumComment.seq!!,
                user         = user
        )).then { Unit }

        val result: ResultActions = this.mockMvc.perform(
                delete("/club/{clubSeq}/album/{clubAlbumSeq}/{clubAlbumCommentSeq}", club.seq, clubAlbum.seq, clubAlbumComment.seq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
                .andDo(document("club-album-comment-remove", getDocumentRequest(), getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스"),
                                parameterWithName("clubAlbumSeq").description("모임 엘범 시퀀스"),
                                parameterWithName("clubAlbumCommentSeq").description("모임 엘범 댓글 시퀀스")
                        ),
                        responseFields(
                                *commonResponseField()
                        )
                ))
    }
}