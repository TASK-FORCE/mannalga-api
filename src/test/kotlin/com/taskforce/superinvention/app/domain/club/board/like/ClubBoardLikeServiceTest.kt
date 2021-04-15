package com.taskforce.superinvention.app.domain.club.board.like

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.app.domain.club.board.ClubBoardRepository
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.role.ClubUserRole
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.common.exception.auth.InsufficientAuthException
import com.taskforce.superinvention.common.exception.club.UserIsNotClubMemberException
import com.taskforce.superinvention.common.exception.club.board.ClubBoardNotFoundException
import com.taskforce.superinvention.config.MockitoHelper
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull

internal class ClubBoardLikeServiceTest {

    lateinit var clubBoardLikeRepository: ClubBoardLikeRepository
    lateinit var clubBoardRepository: ClubBoardRepository
    lateinit var clubUserRepository: ClubUserRepository
    lateinit var clubBoardLikeService: ClubBoardLikeService

    lateinit var user: User
    lateinit var club: Club
    lateinit var clubUser: ClubUser
    lateinit var clubBoard: ClubBoard
    lateinit var roleService: RoleService

    @BeforeEach
    fun init() {
        user = User("cute eric").apply { seq = 562354 }
        club = Club("mock name", "mock desc", 10, "mainImageURL-url.com").apply { seq = 152125 }
        clubUser = ClubUser(club, user).apply { seq = 903125 }
        clubBoard = ClubBoard("게시판 제목", "게시판 내용", clubUser, club, ClubBoard.Category.NORMAL, false).apply { seq = 5153333 }


        clubBoardLikeRepository = mockk()
        clubBoardRepository = mockk()
        clubUserRepository = mockk()
        roleService = mockk()

        clubBoardLikeService = ClubBoardLikeService(
            clubBoardLikeRepository,
            clubBoardRepository,
            clubUserRepository,
            roleService
        )
    }

    @Test
    fun `최초로 모임원이 좋아요를 누를 때 성공해야 한다`() {
        // given
        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, user) }.returns(clubUser)
        every { clubBoardRepository.findByIdOrNull(clubBoard.seq!!) }.returns(clubBoard)
        every { clubBoardLikeRepository.findByClubBoardAndClubUser(clubBoard, clubUser) }.returns(null)
        every { clubBoardLikeRepository.save(any()) }.returns(mockk())
        every { clubBoardLikeRepository.getClubBoardLikeCnt(clubBoard) }.returns(1)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(true)

        // when
        val result = clubBoardLikeService.registerClubBoardLike(user, club.seq!!, clubBoard.seq!!)

        // then
        assertNotNull(result)
        assertEquals(club.seq!!, result.clubSeq)
        assertEquals(clubBoard.seq!!, result.clubBoardSeq)
        assertEquals(1, result.likeCnt)
    }

    @Test
    fun `이미 좋아요를 누른 상태에서 좋아요를 또 눌렀을 경우 저장하지 않고 무시한다`() {
        // given
        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, user) }.returns(clubUser)
        every { clubBoardRepository.findByIdOrNull(clubBoard.seq!!) }.returns(clubBoard)
        every { clubBoardLikeRepository.findByClubBoardAndClubUser(clubBoard, clubUser) }.returns(mockk())
        every { clubBoardLikeRepository.save(any()) }.throws(Exception())
        every { clubBoardLikeRepository.getClubBoardLikeCnt(clubBoard) }.returns(1)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(true)

        // when
        val result = clubBoardLikeService.registerClubBoardLike(user, club.seq!!, clubBoard.seq!!)

        // then
        assertNotNull(result)
        assertEquals(club.seq!!, result.clubSeq)
        assertEquals(clubBoard.seq!!, result.clubBoardSeq)
        assertEquals(1, result.likeCnt)
    }
    
    @Test
    fun `가입한적 없는 유저가 좋아요를 누를 때 실패해야 한다`() {
        // given
        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, user) }.returns(null)

        // when & then
        assertThrows<UserIsNotClubMemberException> { clubBoardLikeService.registerClubBoardLike(user, club.seq!!, clubBoard.seq!!) }
    }

    @Test
    fun `탈퇴한 모임원이 좋아요를 누를 때 실패해야 한다`() {
        // given
        clubUser.clubUserRoles = mutableSetOf(ClubUserRole(clubUser, MockitoHelper.getRoleByRoleName(Role.RoleName.MEMBER, 2)))
        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, user) }.returns(clubUser)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(false)

        // when & then
        assertThrows<InsufficientAuthException> { clubBoardLikeService.registerClubBoardLike(user, club.seq!!, clubBoard.seq!!) }
    }

    @Test
    fun `강퇴당한 모임원이 좋아요를 누를 때 실패해야 한다`() {
        // given
        clubUser.clubUserRoles = mutableSetOf(ClubUserRole(clubUser, MockitoHelper.getRoleByRoleName(Role.RoleName.NONE, 1)))
        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, user) }.returns(clubUser)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(false)

        // when & then
        assertThrows<InsufficientAuthException> { clubBoardLikeService.registerClubBoardLike(user, club.seq!!, clubBoard.seq!!) }
    }

    @Test
    fun `삭제된 게시글에 좋아요를 누를 때 실패해야 한다`() {
        // given
        every { roleService.hasClubMemberAuth(clubUser) }.returns(true)
        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, user) }.returns(clubUser)
        every { clubBoardRepository.findByIdOrNull(clubBoard.seq!!) }.returns(null)

        // when & then
        assertThrows<ClubBoardNotFoundException> { clubBoardLikeService.registerClubBoardLike(user, club.seq!!, clubBoard.seq!!) }
    }

    @Test
    fun `좋아요를 한 모임원이 좋아요 취소를 누를 때 성공해야 한다`() {
        // given
        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, user) }.returns(clubUser)
        every { clubBoardRepository.findByIdOrNull(clubBoard.seq!!) }.returns(clubBoard)
        every { clubBoardLikeRepository.findByClubBoardAndClubUser(clubBoard, clubUser) }.returns(mockk())
        every { clubBoardLikeRepository.delete(any()) }.returns(mockk())
        every { clubBoardLikeRepository.getClubBoardLikeCnt(clubBoard) }.returns(0)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(true)

        // when
        val result = clubBoardLikeService.removeClubBoardLike(user, club.seq!!, clubBoard.seq!!)

        // then
        assertNotNull(result)
        assertEquals(club.seq!!, result.clubSeq)
        assertEquals(clubBoard.seq!!, result.clubBoardSeq)
        assertEquals(0, result.likeCnt)
    }

    @Test
    fun `좋아요를 누르지 않은 모임원이 좋아요 취소를 누를 때 delete를 호출하지 않아야 한고 무시해야 한다`() {
        // given
        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, user) }.returns(clubUser)
        every { clubBoardRepository.findByIdOrNull(clubBoard.seq!!) }.returns(clubBoard)
        every { clubBoardLikeRepository.findByClubBoardAndClubUser(clubBoard, clubUser) }.returns(null)
        every { clubBoardLikeRepository.delete(any()) }.throws(Exception())
        every { clubBoardLikeRepository.getClubBoardLikeCnt(clubBoard) }.returns(0)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(true)

        // when
        val result = clubBoardLikeService.removeClubBoardLike(user, club.seq!!, clubBoard.seq!!)

        // then
        assertNotNull(result)
        assertEquals(club.seq!!, result.clubSeq)
        assertEquals(clubBoard.seq!!, result.clubBoardSeq)
        assertEquals(0, result.likeCnt)
    }

    @Test
    fun `가입한적 없는 유저가 좋아요 취소를 누를 때 실패해야 한다`() {
        // given
        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, user) }.returns(null)

        // when & then
        assertThrows<UserIsNotClubMemberException> { clubBoardLikeService.removeClubBoardLike(user, club.seq!!, clubBoard.seq!!) }

    }

    @Test
    fun `탈퇴한 모임원이 좋아요 취소를 누를 때 실패해야 한다`() {
        // given
        clubUser.clubUserRoles = mutableSetOf(ClubUserRole(clubUser, MockitoHelper.getRoleByRoleName(Role.RoleName.MEMBER, 2)))
        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, user) }.returns(clubUser)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(false)

        // when & then
        assertThrows<InsufficientAuthException> { clubBoardLikeService.removeClubBoardLike(user, club.seq!!, clubBoard.seq!!) }
    }

    @Test
    fun `강퇴당한 모임원이 좋아요 취소를 누를 때 실패해야 한다`() {
        // given
        clubUser.clubUserRoles = mutableSetOf(ClubUserRole(clubUser, MockitoHelper.getRoleByRoleName(Role.RoleName.NONE, 1)))
        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, user) }.returns(clubUser)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(false)

        // when & then
        assertThrows<InsufficientAuthException> { clubBoardLikeService.removeClubBoardLike(user, club.seq!!, clubBoard.seq!!) }
    }

    @Test
    fun `삭제된 게시글에 좋아요 취소를 누를 때 실패해야 한다`() {
        // given
        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, user) }.returns(clubUser)
        every { clubBoardRepository.findByIdOrNull(clubBoard.seq!!) }.returns(null)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(true)

        // when & then
        assertThrows<ClubBoardNotFoundException> { clubBoardLikeService.removeClubBoardLike(user, club.seq!!, clubBoard.seq!!) }
    }
}