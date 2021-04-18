package com.taskforce.superinvention.app.domain.club.board

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.board.img.ClubBoardImgService
import com.taskforce.superinvention.app.domain.club.board.like.ClubBoardLikeRepository
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.role.ClubUserRole
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardEditBody
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardRegisterBody
import com.taskforce.superinvention.app.web.dto.club.board.img.ClubBoardImgEditS3Path
import com.taskforce.superinvention.common.exception.auth.InsufficientAuthException
import com.taskforce.superinvention.common.exception.auth.WithdrawClubUserNotAllowedException
import com.taskforce.superinvention.common.exception.club.UserIsNotClubMemberException
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import com.taskforce.superinvention.config.MockitoHelper
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


internal class ClubBoardServiceTest {
    lateinit var roleService: RoleService;
    lateinit var clubBoardImgService: ClubBoardImgService
    lateinit var clubBoardLikeRepository: ClubBoardLikeRepository
    lateinit var clubBoardRepository: ClubBoardRepository
    lateinit var clubUserRepository: ClubUserRepository
    lateinit var clubBoardService: ClubBoardService

    val imgHost: String =  "mockhost.eric.cc/img"

    lateinit var user: User
    lateinit var club: Club
    lateinit var clubUser: ClubUser
    lateinit var clubBoard: ClubBoard

    @BeforeEach
    fun init() {
        user = User("cute eric").apply { seq = 562354 }
        club = Club("mock name", "mock desc", 10, "mainImageURL-url.com").apply { seq = 152125 }
        clubUser  = ClubUser(club, user).apply { seq = 903125 }
        clubBoard = ClubBoard("게시판 제목", "게시판 내용", clubUser, club, ClubBoard.Category.NORMAL).apply { seq = 5153333 }

        roleService = mockk()
        clubBoardImgService = mockk()
        clubBoardLikeRepository = mockk()
        clubBoardRepository = mockk()
        clubUserRepository = mockk()

        clubBoardService = ClubBoardService(
            roleService,
            clubBoardImgService,
            clubBoardLikeRepository,
            clubBoardRepository,
            clubUserRepository,
            imgHost
        )
    }


    @Test
    fun `모임원이 아닌 유저가 게시글 등록을 요청하면 실패해야한다`() {
        // given
        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, user) }.returns(null)

        // when & then
        assertThrows<UserIsNotClubMemberException> { clubBoardService.registerClubBoard(user, club.seq!!, mockk()) }
    }

    @Test
    fun `강퇴된 모임원이 게시글 등록을 요청하면 실패해야 한다`() {
        // given
        val registerBody = ClubBoardRegisterBody(
            title   = "제목",
            content = "내용",
            category = ClubBoard.Category.NORMAL
        )

        clubUser.clubUserRoles = mutableSetOf(ClubUserRole(clubUser, MockitoHelper.getRoleByRoleName(Role.RoleName.NONE, 1)))
        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, user) }.returns(clubUser)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(false)

        // when & then
        assertThrows<WithdrawClubUserNotAllowedException> { clubBoardService.registerClubBoard(user, club.seq!!, registerBody) }
    }

    @Test
    fun `탈퇴한 모임원이 게시글 등록을 요청하면 실패해야 한다`() {
        // given
        val registerBody = ClubBoardRegisterBody(
            title   = "제목",
            content = "내용",
            category = ClubBoard.Category.NORMAL
        )

        clubUser.clubUserRoles = mutableSetOf(ClubUserRole(clubUser, MockitoHelper.getRoleByRoleName(Role.RoleName.MEMBER, 2)))
        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, user) }.returns(clubUser)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(false)

        // when & then
        assertThrows<WithdrawClubUserNotAllowedException> { clubBoardService.registerClubBoard(user, club.seq!!, registerBody) }
    }

    @Test
    fun `모임원이 공지사항 카테고리글 등록을 요청하면 실패해야 한다`() {
        // given
        val body = ClubBoardRegisterBody(
            title = "게시글 제목은 이렇게 지어야함",
            content = """
                게시글 내용은 이렇게
                하면 어떨까
                에릭과 함께하는
                Super Invention!
            """.trimIndent(),
            category = ClubBoard.Category.NOTICE
        )
        every { roleService.hasClubManagerAuth(clubUser) }.returns(false)
        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, user) }.returns(clubUser)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(false)

        // when & then
        assertThrows<InsufficientAuthException> { clubBoardService.registerClubBoard(user, club.seq!!, body) }
    }

    @Test
    fun `모임장 및 매니저는 게시글 삭제가 가능하다`() {
        // given
        val actorUser: User = mockk()
        val actorClubUser: ClubUser = mockk()
        every { clubUserRepository.findByClubAndUser(club, actorUser) }.returns(actorClubUser)
        every { clubBoardRepository.findBySeq(clubBoard.seq!!) }.returns(clubBoard)
        every { roleService.hasClubManagerAuth(actorClubUser) }.returns(true)
        every { clubBoardImgService.softDeleteImageAllInClubBoard(clubBoard) }.returns(Unit)

        // when
        clubBoardService.deleteClubBoard(actorUser, clubBoard.seq!!)

        // then
        verify { clubBoardImgService.softDeleteImageAllInClubBoard(clubBoard) }
        confirmVerified(clubBoardImgService)
    }

    @Test
    fun `게시글 작성자가 다른 유저는 게시글은 수정할 수 없다`() {
        // given
        val actorUser: User = mockk()
        val actorClubUser: ClubUser = mockk()

        every { clubUserRepository.findByClubAndUser(club, actorUser) }.returns(actorClubUser)
        every { clubBoardRepository.findBySeq(clubBoard.seq!!) }.returns(clubBoard)
        every { roleService.hasClubManagerAuth(actorClubUser) }.returns(true)
        every { clubBoardImgService.softDeleteImageAllInClubBoard(clubBoard) }.returns(Unit)

        // when
        clubBoardService.deleteClubBoard(actorUser, clubBoard.seq!!)

        // then
        verify { clubBoardImgService.softDeleteImageAllInClubBoard(clubBoard) }
        confirmVerified(clubBoardImgService)
    }

    @Test
    fun `모임 매니저와 관리자는 다른 모임원의 게시글을 수정할 수 없다`() {

        // given
        val actorUser: User = mockk()
        val actorClubUser: ClubUser = mockk()
        val editBody = ClubBoardEditBody(
            title    = "sss",
            content  = "",
            category = ClubBoard.Category.NORMAL,
            imageList = listOf(
                ClubBoardImgEditS3Path(
                    imgSeq = null,
                    img = S3Path(
                        absolutePath = "신규이미지-절대경로",
                        filePath     = "신규이미지 상대 경로",
                        fileName     = "신규이미지 이름"
                    )
                )
            )
        )

        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, actorUser) }.returns(actorClubUser)
        every { clubBoardRepository.findBySeqWithWriter(clubBoard.seq!!) }.returns(clubBoard)
        every { roleService.hasClubManagerAuth(actorClubUser) }.returns(true)
        every { clubBoardImgService.softDeleteImageBySeqIn(any()) }.returns(Unit)

        // when & then
        assertThrows<InsufficientAuthException> { clubBoardService.editClubBoard(actorUser, club.seq!!, clubBoard.seq!!, editBody)}
    }

    @Test
    fun `작성자가 모임을 탈퇴하면 게시글을 수정할 수 없다`() {

        // given
        val actorUser    : User = mockk()
        val actorClubUser: ClubUser = mockk()
        val editBody = ClubBoardEditBody(
            title    = "sss",
            content  = "",
            category = ClubBoard.Category.NORMAL,
            imageList = listOf(
                ClubBoardImgEditS3Path(
                    imgSeq = null,
                    img = S3Path(
                        absolutePath = "신규이미지-절대경로",
                        filePath     = "신규이미지 상대 경로",
                        fileName     = "신규이미지 이름"
                    )
                )
            )
        )

        every { clubUserRepository.findByClubSeqAndUser(club.seq!!, actorUser) }.returns(actorClubUser)
        every { clubBoardRepository.findBySeqWithWriter(clubBoard.seq!!) }.returns(clubBoard)
        every { roleService.hasClubMemberAuth(actorClubUser) }.returns(false)
        every { clubBoardImgService.softDeleteImageBySeqIn(any()) }.returns(Unit)

        // when & then
        assertThrows<InsufficientAuthException> { clubBoardService.editClubBoard(actorUser, club.seq!!, clubBoard.seq!!, editBody)}
    }

    @Test
    fun `게시글 작성자는 게시글 삭제가 가능하다`() {

        // given
        every { clubUserRepository.findByClubAndUser(club, user) }.returns(clubUser)
        every { clubBoardRepository.findBySeq(clubBoard.seq!!) }.returns(clubBoard)
        every { roleService.hasClubManagerAuth(clubUser) }.returns(false)
        every { clubBoardImgService.softDeleteImageAllInClubBoard(clubBoard) }.returns(Unit)

        // when
        clubBoardService.deleteClubBoard(user, clubBoard.seq!!)

        // then
        verify { clubBoardImgService.softDeleteImageAllInClubBoard(clubBoard) }
        confirmVerified(clubBoardImgService)
    }

    @Test
    fun `작성자가 아니며, 매니저 및 모임장이 아닌 모임원은 게시글 삭제가 불가능하다`() {
        // given
        val actorUser: User = mockk()
        val actorClubUser: ClubUser = mockk()
        every { clubUserRepository.findByClubAndUser(club, actorUser) }.returns(actorClubUser)
        every { clubBoardRepository.findBySeq(clubBoard.seq!!) }.returns(clubBoard)
        every { roleService.hasClubManagerAuth(actorClubUser) }.returns(false)
        every { clubBoardImgService.softDeleteImageAllInClubBoard(clubBoard) }.returns(Unit)

        // when & then
        assertThrows<InsufficientAuthException> { clubBoardService.deleteClubBoard(actorUser, clubBoard.seq!!) }
    }
}

