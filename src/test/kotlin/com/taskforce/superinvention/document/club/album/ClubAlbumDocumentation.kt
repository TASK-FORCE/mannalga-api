package com.taskforce.superinvention.document.club.album

import com.ninjasquad.springmockk.MockkBean
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.album.ClubAlbum
import com.taskforce.superinvention.app.domain.club.album.ClubAlbumService
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.role.ClubUserRole
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.role.RoleGroup
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.controller.club.album.ClubAlbumController
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumDto
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumListDto
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumRegisterDto
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumSearchOption
import com.taskforce.superinvention.app.web.dto.common.PageDto
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
import org.springframework.data.domain.Page
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(ClubAlbumController::class)
class ClubAlbumDocumentation: ApiDocumentationTestV2() {

    @MockkBean(relaxed = true)
    lateinit var clubAlbumService: ClubAlbumService

    lateinit var club: Club
    lateinit var user: User
    lateinit var clubUser : ClubUser
    lateinit var clubAlbum: ClubAlbum

    @BeforeEach
    fun setup() {
        club = Club(
                name = "테스트 클럽",
                description   = "",
                maximumNumber = 10,
                mainImageUrl  = ""
        ).apply { seq = 88 }

        user = User ("12345").apply { seq = 2 }
        clubUser = ClubUser(club, user, isLiked = true).apply { seq = 110 }
        clubUser.clubUserRoles = mutableSetOf(
            ClubUserRole(clubUser, Role(Role.RoleName.CLUB_MEMBER, RoleGroup("ROLE_NAME", "ROLE_GROUP_TYPE"), 2))
        )

        clubAlbum = ClubAlbum (
                club        = club,
                writer    = clubUser,
                title       = "클럽 사진첩 사진 1",
                img_url     = "이미지 URL",
                file_name   = "파일 이름",
                delete_flag = false
        ).apply { seq = 100 }
    }

    @Test
    fun `모임 사진첩 사진 목록 조회`() {

        // given
        val clubAlbumListDto = ClubAlbumListDto(clubAlbum)

        val pageable: Pageable = PageRequest.of(0, 20)
        val clubAlbumList: List<ClubAlbumListDto> = listOf(clubAlbumListDto)
        val searchOpt = ClubAlbumSearchOption(title = "사진첩")

        val clubAlbums: Page<ClubAlbumListDto> = PageImpl(clubAlbumList, pageable, clubAlbumList.size.toLong())

        // when
        every {
            clubAlbumService.getClubAlbumList(
                club.seq!!,
                match { search -> search.title == "사진첩" },
                pageable
        )}.returns(PageDto(clubAlbums))

        val result: ResultActions = this.mockMvc.perform(
                get("/club/{clubSeq}/album", club.seq)
                        .queryParam("title", searchOpt.title)
                        .queryParam("page", "0")
                        .queryParam("size", "20")
                        .header("Authorization", "Bearer xxxxxxxxxxxxx")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
                .andDo(document("club-album-select", getDocumentRequest(), getDocumentResponse(),
                        requestParameters(
                                *commonPageQueryParam(),
                                parameterWithName("title").description("제목")
                        ),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스")
                        ),
                        responseFields(
                                *commonResponseField(),
                                *pageFieldDescriptor(),
                                fieldWithPath("data.content[].albumSeq").type(JsonFieldType.NUMBER).description("사진첩 seq"),
                                fieldWithPath("data.content[].title").type(JsonFieldType.STRING).description("사진첩 제목")   ,
                                fieldWithPath("data.content[].file_name").type(JsonFieldType.STRING).description("파일 명")  ,
                                fieldWithPath("data.content[].imgUrl").type(JsonFieldType.STRING).description("이미지 URL")  ,
                                fieldWithPath("data.content[].likeCnt").type(JsonFieldType.NUMBER).description("엘범 좋아요 개수")  ,
                                fieldWithPath("data.content[].commentCnt").type(JsonFieldType.NUMBER).description("엘범 댓글 개수")  ,
                                fieldWithPath("data.content[].writerClubUserSeq").type(JsonFieldType.NUMBER).description("작성자 clubUser seq")
                        )
                    )
                )
    }

    @Test
    fun `모임 사진첩 사진 단건 조회`() {

        // given
        every {clubAlbumService.getClubAlbumDto(any(), club.seq!!, clubAlbum.seq)}
            .returns(ClubAlbumDto(clubAlbum = clubAlbum, isLiked = false))

        // when

        val result: ResultActions = this.mockMvc.perform(
            get("/club/{clubSeq}/album/{clubAlbumSeq}", club.seq, clubAlbum.seq)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
            .andDo(document("club-album-select-single", getDocumentRequest(), getDocumentResponse(),
                pathParameters(
                    parameterWithName("clubSeq").description("모임 시퀀스"),
                    parameterWithName("clubAlbumSeq").description("모임 사진첩 시퀀스")
                ),
                responseFields(
                    *commonResponseField(),
                    fieldWithPath("data.albumSeq").type(JsonFieldType.NUMBER).description("사진첩 seq"),
                    fieldWithPath("data.title").type(JsonFieldType.STRING).description("사진첩 제목"),
                    fieldWithPath("data.file_name").type(JsonFieldType.STRING).description("파일 명"),
                    fieldWithPath("data.imgUrl").type(JsonFieldType.STRING).description("사진첩 이미지 URL"),
                    fieldWithPath("data.isLiked").type(JsonFieldType.BOOLEAN).description("조회자가 좋아요를 눌렀는지"),
                    fieldWithPath("data.likeCnt").type(JsonFieldType.NUMBER).description("좋아요 개수"),
                    fieldWithPath("data.commentCnt").type(JsonFieldType.NUMBER).description("댓글 개수"),
                    fieldWithPath("data.writer.writerUserSeq").type(JsonFieldType.NUMBER).description("댓글 개수"),
                    fieldWithPath("data.writer.writerClubUserSeq").type(JsonFieldType.NUMBER).description("댓글 개수"),
                    fieldWithPath("data.writer.name").type(JsonFieldType.STRING).description("작성자명 (user)"),
                    fieldWithPath("data.writer.imgUrl").type(JsonFieldType.STRING).description("작성자 프로필 url"),
                    fieldWithPath("data.writer.role[]").type(JsonFieldType.ARRAY).description("작성자 권한")
                )
            )
        )
    }


    @Test
    @WithMockUser(username = "sight", authorities = [Role.CLUB_MEMBER])
    fun `모임 사진첩 등록`() {

        // given
        val body = ClubAlbumRegisterDto(
                title     = "신규 모임 사진첩 제목",
                file_name = "파일명",
                imgUrl    = "이미지 URL"
        )

        // when
//        every { (clubAlbumService.registerClubAlbum(eq(user), eq(club.seq!!), eq(body))) }.then

        val result: ResultActions = this.mockMvc.perform(
                post("/club/{clubSeq}/album", club.seq!!)
                        .content(objectMapper.writeValueAsString(body))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isCreated)
                .andDo(document("club-album-register", getDocumentRequest(), getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스")
                        ),
                        requestFields(
                                fieldWithPath("title").type(JsonFieldType.STRING).description("제목"),
                                fieldWithPath("file_name").type(JsonFieldType.STRING).description("파일명"),
                                fieldWithPath("imgUrl").type(JsonFieldType.STRING).description("이미지 URL")
                        ),
                        responseFields(
                                *commonResponseField()
                        )
                ))
    }

    @Test
    @WithMockUser(username = "sight", authorities = [Role.CLUB_MEMBER])
    fun `모임 게시판 글 목록 삭제`() {

        //  when
//        every { (clubAlbumService.removeClubAlbum(user, club.seq!!, clubAlbum.seq!!) }.then{ Unit }

        val result: ResultActions = this.mockMvc.perform(
                delete("/club/{clubSeq}/album/{clubAlbumSeq}", club.seq, clubAlbum.seq)
                        .header("Authorization", "Bearer xxxxxxxxxxx")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
                .andDo(document("club-album-remove", getDocumentRequest(), getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스"),
                                parameterWithName("clubAlbumSeq").description("모임 사진첩 시퀀스")
                        ),
                        responseFields(
                                *commonResponseField()
                        )
                ))
    }
}