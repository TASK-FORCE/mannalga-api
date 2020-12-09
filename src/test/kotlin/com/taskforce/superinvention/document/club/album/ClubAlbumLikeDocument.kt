package com.taskforce.superinvention.document.club.album

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.album.ClubAlbum
import com.taskforce.superinvention.app.domain.club.album.like.ClubAlbumLike
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumRegisterDto
import com.taskforce.superinvention.app.web.dto.club.album.comment.ClubAlbumCommentRegisterDto
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonResponseField
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.test.ApiDocumentationTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.*
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

class ClubAlbumLikeDocument: ApiDocumentationTest() {

    lateinit var club: Club
    lateinit var user: User
    lateinit var clubUser : ClubUser
    lateinit var clubAlbum: ClubAlbum
    lateinit var clubAlbumLike: ClubAlbumLike

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
    @WithMockUser(username = "sight", authorities = [Role.CLUB_MEMBER])
    fun `모임 사진첩 좋아요 등록`() {

        // when
        `when`(clubAlbumLikeService.registerClubAlbumLike(
                clubSeq = club.seq!!,
                clubAlbumSeq = clubAlbum.seq!!,
                user = user
        )).then { Unit }

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
                                *commonResponseField()
                        )
                ))
    }

    @Test
    @WithMockUser(username = "sight", authorities = [Role.CLUB_MEMBER])
    fun `모임 사진첩 좋아요 삭제`() {

        // when
        `when`(clubAlbumLikeService.removeClubAlbumLike(
                clubSeq = club.seq!!,
                clubAlbumSeq = clubAlbum.seq!!,
                user = user
        )).then { Unit }

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
                                *commonResponseField()
                        )
                ))
    }
}