package com.taskforce.superinvention.app.domain.club.board.comment

import com.taskforce.superinvention.app.domain.club.board.ClubBoardService
import com.taskforce.superinvention.app.domain.club.user.ClubUserService
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.board.comment.ClubBoardCommentListDto
import com.taskforce.superinvention.app.web.dto.club.board.comment.ClubBoardCommentRegisterDto
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.common.exception.ResourceNotFoundException
import com.taskforce.superinvention.common.exception.auth.OnlyWriterCanAccessException
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClubBoardCommentService(
    private val commentRepository: ClubBoardCommentRepository,
    private val clubBoardService: ClubBoardService,
    private val clubUserService: ClubUserService,
    private val roleService: RoleService
) {

    fun getValidCommentBySeq(commentSeq: Long): ClubBoardComment {
        return commentRepository.findByIdOrNull(commentSeq)
            ?: throw ResourceNotFoundException("댓글이 삭제되었거나, 존재하지 않습니다.")
    }

    // 댓글 루트레벨만 조회
    @Transactional(readOnly = true)
    fun getCommentList(user: User?, pageable: Pageable?, clubBoardSeq: Long): PageDto<ClubBoardCommentListDto> {

        val pageList = commentRepository.findRootCommentListWithWriter(pageable!!, clubBoardSeq)

        return if(user != null) {
            PageDto(pageList.map{ comment -> ClubBoardCommentListDto(comment, user) })
        } else {
            PageDto(pageList.map(::ClubBoardCommentListDto))
        }
    }

    // 특정 댓글의 뎁스별 하위 댓글 조회
    @Transactional(readOnly = true)
    fun getChildCommentList(user: User?, parentCommentSeq: Long, depthLimitCnt: Long = 1): List<ClubBoardCommentListDto> {
        val parentComment = getValidCommentBySeq(parentCommentSeq)

        val pageList: List<ClubBoardCommentCTE> = commentRepository.findChildCommentsWithWriter(
            parentCommentSeq = parentComment.seq!!,
            startDepth = parentComment.depth + 1,
            limitDepth = parentComment.depth + depthLimitCnt
        )

        return if(user != null) {
            pageList.map{ comment -> ClubBoardCommentListDto(comment, user) }
        } else {
            pageList.map(::ClubBoardCommentListDto)
        }
    }

    // 댓글 등록
    @Transactional
    fun registerComment(clubSeq          : Long,
                        clubBoardSeq     : Long,
                        parentCommentSeq : Long?,
                        user: User,
                        body: ClubBoardCommentRegisterDto
    ): ClubBoardComment {

        val clubUser  = clubUserService.getValidClubUser(clubSeq, user)
        val clubBoard = clubBoardService.getValidClubBoardBySeq(clubBoardSeq)

        var parentComment: ClubBoardComment?= null
        var depth = 1L

        if(parentCommentSeq != null) {
            parentComment = getValidCommentBySeq(parentCommentSeq)
            depth = parentComment.depth + 1L
        }

        val comment = ClubBoardComment(
            content = body.content,
            clubUser = clubUser,
            clubBoard = clubBoard,
            parent = parentComment,
            depth = depth
        )

        return commentRepository.save(comment)
    }

    // 댓글 수정
    @Transactional
    fun editComment(
        clubSeq       : Long,
        clubBoardSeq  : Long,
        clubBoardCommentSeq: Long,
        user: User,
        body: ClubBoardCommentRegisterDto
    ) {

        val clubUser = clubUserService.getValidClubUser(clubSeq, user)
        val comment  = getValidCommentBySeq(clubBoardCommentSeq)

        if(comment.clubUser != clubUser) {
            throw OnlyWriterCanAccessException("댓글 작성자만 수정 할 수 있습니다.")
        }

        comment.content = body.content
    }

    @Transactional
    fun removeComment(clubSeq: Long, clubBoardSeq: Long, clubBoardCommentSeq: Long, user: User) {
        val clubUser = clubUserService.getValidClubUser(clubSeq, user)
        val comment  = getValidCommentBySeq(clubBoardCommentSeq)

        // 관리자이거나, 작성자만 삭제 가능
        if(!roleService.hasClubManagerAuth(clubUser) && clubUser != comment.clubUser) {
            throw OnlyWriterCanAccessException("댓글 작성자와 관리자만 삭제 할 수 있습니다.")
        }

        commentRepository.delete(comment)
    }
}