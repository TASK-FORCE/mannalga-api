package com.taskforce.superinvention.document.club.album

import com.ninjasquad.springmockk.MockkBean
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.album.ClubAlbum
import com.taskforce.superinvention.app.domain.club.album.like.ClubAlbumLike
import com.taskforce.superinvention.app.domain.club.album.like.ClubAlbumLikeService
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.controller.club.album.ClubAlbumLikeController
import com.taskforce.superinvention.app.web.dto.club.album.like.ClubAlbumLikeDto
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

@WebMvcTest(ClubAlbumLikeController::class)
class ClubAlbumLikeDocument: ApiDocumentationTestV2() {

    private lateinit var club: Club
    private lateinit var user: User
    private lateinit var clubUser : ClubUser
    private lateinit var clubAlbum: ClubAlbum
    private lateinit var clubAlbumLike: ClubAlbumLike

    @MockkBean
    private lateinit var clubAlbumLikeService: ClubAlbumLikeService

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

        clubAlbum = ClubAlbum (
                club = club,
                writer    = clubUser,
                title       = "모임 사진첩 사진 1",
                img_url     = "이미지 URL",
                file_name   = "파일 이름",
                delete_flag = false
        ).apply { seq = 100 }

        clubAlbumLike = ClubAlbumLike(
                clubUser  = clubUser,
                clubAlbum = clubAlbum
        ).apply { seq = 111 }

    }

    @Test
    @WithMockUser(username = "sight")
    fun `모임 사진첩 좋아요 등록`() {

        // given
        val clubAlbumLikeDto = ClubAlbumLikeDto(
            clubAlbumSeq = clubAlbum.seq!!,
            clubSeq      = club.seq!!,
            likeCnt      = 20
        )

        every {
            clubAlbumLikeService.registerClubAlbumLike(
                clubSeq = club.seq!!,
                clubAlbumSeq = clubAlbum.seq!!,
                user = any()
            )
        } returns clubAlbumLikeDto

        // when
        val result: ResultActions = this.mockMvc.perform(
                post("/club/{clubSeq}/album/{clubAlbumSeq}/like", club.seq, clubAlbum.seq)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isCreated)
                .andDo(document(
                        "club-album-like-register",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("clubSeq").description("모임 시퀀스"),
                                parameterWithName("clubAlbumSeq").description("모임 엘범 시퀀스")
                        ),
                        responseFields(
                                *commonResponseField(),
                                fieldWithPath("data.clubSeq").type(JsonFieldType.NUMBER).description("클럽 seq"),
                                fieldWithPath("data.clubAlbumSeq").type(JsonFieldType.NUMBER).description("사진첩 seq"),
                                fieldWithPath("data.likeCnt").type(JsonFieldType.NUMBER).description("좋아요 개수")
                        )
                ))
    }

    @Test
    fun `모임 사진첩 좋아요 삭제`() {
        // given
        val clubAlbumLikeDto = ClubAlbumLikeDto(
            clubAlbumSeq = clubAlbum.seq!!,
            clubSeq      = club.seq!!,
            likeCnt      = 20
        )

        every {clubAlbumLikeService.removeClubAlbumLike(
            clubSeq = club.seq!!,
            clubAlbumSeq = clubAlbum.seq!!,
            user = any()
        )} returns clubAlbumLikeDto

        // when
        val result: ResultActions = this.mockMvc.perform(
            delete("/club/{clubSeq}/album/{clubAlbumSeq}/like", club.seq, clubAlbum.seq)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
            .andDo(document(
                "club-album-like-remove",
                getDocumentRequest(),
                getDocumentResponse(),
                pathParameters(
                    parameterWithName("clubSeq").description("모임 시퀀스"),
                    parameterWithName("clubAlbumSeq").description("모임 엘범 시퀀스")
                ),
                responseFields(
                    *commonResponseField(),
                    fieldWithPath("data.clubSeq").type(JsonFieldType.NUMBER).description("클럽 seq"),
                    fieldWithPath("data.clubAlbumSeq").type(JsonFieldType.NUMBER).description("사진첩 seq"),
                    fieldWithPath("data.likeCnt").type(JsonFieldType.NUMBER).description("좋아요 개수")
                )
            ))
    }
}