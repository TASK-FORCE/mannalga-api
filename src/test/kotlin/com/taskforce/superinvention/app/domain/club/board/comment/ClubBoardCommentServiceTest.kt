package com.taskforce.superinvention.app.domain.club.board.comment

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.app.domain.club.board.ClubBoardService
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserService
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.board.comment.ClubBoardCommentRegisterDto
import com.taskforce.superinvention.common.exception.BizException
import com.taskforce.superinvention.common.exception.auth.InsufficientAuthException
import com.taskforce.superinvention.common.exception.auth.OnlyWriterCanAccessException
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class ClubBoardCommentServiceTest {

    lateinit var clubBoardCommentRepository: ClubBoardCommentRepository
    lateinit var clubBoardService: ClubBoardService
    lateinit var clubUserService: ClubUserService
    lateinit var roleService: RoleService

    lateinit var clubBoardCommentService: ClubBoardCommentService

    lateinit var club: Club
    lateinit var user: User
    lateinit var clubUser: ClubUser
    lateinit var clubBoard: ClubBoard
    lateinit var clubBoardCommentRegisterDto: ClubBoardCommentRegisterDto
    lateinit var clubBoardComment: ClubBoardComment

    @BeforeEach
    fun init() {
        clubBoardCommentRepository = mockk()
        clubBoardService = mockk()
        clubUserService = mockk()
        roleService = mockk()

        clubBoardCommentService = ClubBoardCommentService(
            clubBoardCommentRepository,
            clubBoardService,
            clubUserService,
            roleService
        )

        club = Club(
            name = "테스트 모임",
            description = "모임 설명",
            maximumNumber = 10,
            mainImageUrl = "asdasd.jpg"
        ).apply { seq = 1132454 }
        user = User("My Eric").apply { seq = 135 }
        clubUser = ClubUser(club, user).apply { 565277 }
        clubBoard = ClubBoard(
            title = "게시판 제목인데",
            content = """
                게시판의 내용은
                이렇게 가는게
                좋을거같다
            """.trimIndent(),
            clubUser = clubUser,
            club = club,
            category = ClubBoard.Category.NORMAL,
            deleteFlag = false
        ).apply { seq = 62346 }

        clubBoardCommentRegisterDto = ClubBoardCommentRegisterDto("댓글은 이러하게 달거입니다")
        clubBoardComment = ClubBoardComment(
            content = "바뀌기 전 댓글",
            clubUser = clubUser,
            clubBoard = clubBoard,
            parent = null,
            depth = 1,
            deleteFlag = false
        ).apply { seq = 99214L }
    }

    @Test
    fun `모임원이 게시글 댓글 등록을 요청하면 성공해야 한다`() {
        // given
        every { clubUserService.getValidClubUser(club.seq!!, user) }.returns(clubUser)
        every { clubBoardService.getValidClubBoardBySeq(clubBoard.seq!!) }.returns(clubBoard)
        every { clubBoardCommentRepository.save(any()) }.returnsArgument(0)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(true)

        // when
        val result = clubBoardCommentService.registerComment(
            club.seq!!,
            clubBoard.seq!!,
            null,
            user,
            clubBoardCommentRegisterDto
        )

        // then
        Assertions.assertEquals(clubBoardCommentRegisterDto.content, result.content)
    }

    @Test
    fun `모임원 아닌 유저가 게시글 댓글 등록을 요청하면 실패해야 한다`() {
        // given
        every { clubUserService.getValidClubUser(club.seq!!, user) }.returns(clubUser)
        every { clubBoardService.getValidClubBoardBySeq(clubBoard.seq!!) }.returns(clubBoard)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(false)


        // when & then
        assertThrows<InsufficientAuthException> {
            clubBoardCommentService.registerComment(
                club.seq!!,
                clubBoard.seq!!,
                null,
                user,
                clubBoardCommentRegisterDto
            )
        }
    }

    @Test
    fun `댓글을 작성한 모임원이 게시글 댓글 수정을 요청하면 성공해야 한다`() {
        // given
        every { clubUserService.getValidClubUser(club.seq!!, user) }.returns(clubUser)
        every { clubBoardCommentService.getValidCommentBySeq(any()) }.returns(clubBoardComment)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(true)

        // when
        clubBoardCommentService.editComment(
            club.seq!!,
            clubBoard.seq!!,
            clubBoardComment.seq!!,
            user,
            ClubBoardCommentRegisterDto("이렇게 수정할겁니다")
        )

        // then
        Assertions.assertEquals("이렇게 수정할겁니다", clubBoardComment.content)
    }

    @Test
    fun `모임원 아닌 유저가 게시글 댓글 수정을 요청하면 실패해야 한다`() {
        // given
        every { clubUserService.getValidClubUser(club.seq!!, user) }.returns(clubUser)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(false)

        // when & then
        assertThrows<InsufficientAuthException> {
            clubBoardCommentService.editComment(
                club.seq!!,
                clubBoard.seq!!,
                clubBoardComment.seq!!,
                user,
                ClubBoardCommentRegisterDto("이렇게 수정할겁니다")
            )
        }
    }

    @Test
    fun `작성자가 아닌 유저가 게시글 댓글 수정을 요청하면 실패해야 한다`() {
        // given
        clubBoardComment.clubUser = ClubUser(mockk(), mockk())
        every { clubUserService.getValidClubUser(club.seq!!, user) }.returns(clubUser)
        every { clubBoardCommentService.getValidCommentBySeq(any()) }.returns(clubBoardComment)
        every { roleService.hasClubMemberAuth(clubUser) }.returns(true)

        // when & then
        assertThrows<OnlyWriterCanAccessException> {
            clubBoardCommentService.editComment(
                club.seq!!,
                clubBoard.seq!!,
                clubBoardComment.seq!!,
                user,
                ClubBoardCommentRegisterDto("이렇게 수정할겁니다")
            )
        }
    }

    @Test
    fun `댓글 작성자가 댓글 삭제를 삭제할 경우 성공해야 한다`() {

        // given
        every { clubUserService.getValidClubUser(club.seq!!, user) }.returns(clubUser)
        every { clubBoardCommentService.getValidCommentBySeq(any()) }.returns(clubBoardComment)
        every { roleService.hasClubManagerAuth(clubUser) }.returns(false)
        every { clubBoardCommentRepository.delete(clubBoardComment) }.returns(mockk())
        every { clubBoardCommentRepository.findChildCommentsWithWriter(clubBoardComment.seq!!) } returns emptyList()

        // when
        clubBoardCommentService.removeComment(club.seq!!, clubBoard.seq!!, clubBoardComment.seq!!, user)

        // then
        verify { clubBoardCommentRepository.delete(clubBoardComment) }
    }

    @Test
    fun `댓글 작성자가 아닌 매니저가 댓글을 삭제할 경우 성공해야 한다`() {

        // given
        val manager = User("MANAGER")
        val managerClubUser = ClubUser(club, manager)
        every { clubUserService.getValidClubUser(club.seq!!, manager) }.returns(managerClubUser)
        every { clubBoardCommentService.getValidCommentBySeq(any()) }.returns(clubBoardComment)
        every { roleService.hasClubManagerAuth(managerClubUser) }.returns(true)
        every { clubBoardCommentRepository.delete(clubBoardComment) }.returns(mockk())
        every { clubBoardCommentRepository.findChildCommentsWithWriter(clubBoardComment.seq!!) } returns emptyList()

        // when
        clubBoardCommentService.removeComment(club.seq!!, clubBoard.seq!!, clubBoardComment.seq!!, manager)

        // then
        verify { clubBoardCommentRepository.delete(clubBoardComment) }
    }

    @Test
    fun `댓글 작성자가 아닌 모임원이 댓글을 삭제할 경우 실패해야 한다`() {
        // given
        val manager = User("OTHER CLUB USER")
        val managerClubUser = ClubUser(club, manager)
        every { clubUserService.getValidClubUser(club.seq!!, manager) }.returns(managerClubUser)
        every { clubBoardCommentService.getValidCommentBySeq(any()) }.returns(clubBoardComment)
        every { roleService.hasClubManagerAuth(managerClubUser) }.returns(false)

        // when & then
        assertThrows<OnlyWriterCanAccessException> {
            clubBoardCommentService.removeComment(club.seq!!, clubBoard.seq!!, clubBoardComment.seq!!, manager)
        }
    }

}
