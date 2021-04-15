package com.taskforce.superinvention.app.domain.club.album.comment

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.album.ClubAlbum
import com.taskforce.superinvention.app.domain.club.album.ClubAlbumService
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserService
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.album.comment.ClubAlbumCommentRegisterDto
import com.taskforce.superinvention.common.exception.ResourceNotFoundException
import com.taskforce.superinvention.common.exception.auth.InsufficientAuthException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

internal class ClubAlbumCommentServiceTest {

    lateinit var clubAlbumCommentService: ClubAlbumCommentService
    lateinit var roleService: RoleService
    lateinit var clubAlbumService: ClubAlbumService
    lateinit var clubUserService: ClubUserService
    lateinit var clubAlbumCommentRepository: ClubAlbumCommentRepository

    lateinit var club: Club
    lateinit var user: User
    lateinit var clubUser: ClubUser
    lateinit var clubAlbum: ClubAlbum
    lateinit var clubAlbumComment: ClubAlbumComment
    val clubAlbumCommentRegisterDto = ClubAlbumCommentRegisterDto("댓글 이렇게 이렇게 달겁니다.")

    @BeforeEach
    fun init() {
        roleService = mockk()
        clubAlbumService = mockk()
        clubUserService = mockk()
        clubAlbumCommentRepository = mockk()

        clubAlbumCommentService = ClubAlbumCommentService(
            roleService,
            clubAlbumService,
            clubUserService,
            clubAlbumCommentRepository
        )


        club = mockk<Club>(relaxed = true).apply {
            seq = 1231254
            name = "CLUB NAME"
            description = "description"
            maximumNumber = 50
            userCount = 10
        }

        user = mockk<User>(relaxed = true).apply {
            seq = 613
        }

        clubUser = ClubUser(
            club = club,
            user = user,
            isLiked = false
        ).apply {
            seq = 123522
            clubUserRoles = mutableSetOf()
        }

        clubAlbum = ClubAlbum(
            club,
            "사진첩은 이런 제목",
            clubUser,
            "mannal.ga?asdmncod/asfgads/ayuer.jpg",
            "ayuer.jpg",
            false
        ).apply { seq = 1356136 }

        clubAlbumComment = ClubAlbumComment(
            content = "이런 댓글을 달면 어떨까?",
            clubUser = clubUser,
            clubAlbum = clubAlbum
        ).apply { seq = 646121 }


    }

    @Test
    fun `모임원의 댓글 등록은 성공해야 한다`() {
        // given
        every { clubUserService.getValidClubUser(club.seq!!, user) }.returns(clubUser)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(true)
        every { clubAlbumService.getValidClubAlbumBySeq(clubAlbum.seq!!) }.returns(clubAlbum)
        every { clubAlbumCommentRepository.save(any()) }.returns(mockk(relaxed = true))

        // when & then
        assertDoesNotThrow {
            clubAlbumCommentService.registerComment(
                club.seq!!,
                clubAlbum.seq!!,
                null,
                user,
                clubAlbumCommentRegisterDto
            )
        }
    }

    @Test
    fun `강퇴 및 탈퇴당한 유저는 댓글등록에 실패해야 한다`() {
        every { clubUserService.getValidClubUser(club.seq!!, user) }.returns(clubUser)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(false)

        assertThrows<InsufficientAuthException> { clubAlbumCommentService.registerComment(
            club.seq!!,
            clubAlbum.seq!!,
            null,
            user,
            clubAlbumCommentRegisterDto
        ) }
    }

    @Test
    fun `존재하지 않는 댓글에 대한 답글은 실패해야 한다`() {
        // given
        val parentCommentSeq = -9858L
        every { clubUserService.getValidClubUser(club.seq!!, user) }.returns(clubUser)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(true)
        every { clubAlbumService.getValidClubAlbumBySeq(clubAlbum.seq!!) }.returns(clubAlbum)
        every { clubAlbumCommentRepository.findByIdOrNull(parentCommentSeq) }.returns(null)

        // when & then
        assertThrows<ResourceNotFoundException> {
            clubAlbumCommentService.registerComment(
                club.seq!!,
                clubAlbum.seq!!,
                parentCommentSeq,
                user,
                clubAlbumCommentRegisterDto
            )
        }
    }


}