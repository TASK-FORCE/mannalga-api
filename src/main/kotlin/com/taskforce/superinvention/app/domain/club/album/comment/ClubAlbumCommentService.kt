package com.taskforce.superinvention.app.domain.club.album.comment

import com.taskforce.superinvention.app.domain.club.album.ClubAlbumService
import com.taskforce.superinvention.app.domain.club.user.ClubUserService
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.album.comment.ClubAlbumCommentListDto
import com.taskforce.superinvention.app.web.dto.club.album.comment.ClubAlbumCommentRegisterDto
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.common.exception.ResourceNotFoundException
import com.taskforce.superinvention.common.exception.auth.InsufficientAuthException
import com.taskforce.superinvention.common.exception.auth.OnlyWriterCanAccessException
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClubAlbumCommentService(
    private val roleService       : RoleService,
    private val clubAlbumService  : ClubAlbumService,
    private val clubUserService   : ClubUserService,
    private val commentRepository : ClubAlbumCommentRepository
) {

    fun getValidCommentBySeq(commentSeq: Long): ClubAlbumComment {
        return commentRepository.findByIdOrNull(commentSeq)
            ?: throw ResourceNotFoundException("댓글이 삭제되었거나, 존재하지 않습니다.")
    }
    
    // 댓글 루트레벨만 조회
    @Transactional(readOnly = true)
    fun getCommentList(user: User?, pageable: Pageable?, clubAlbumSeq: Long): PageDto<ClubAlbumCommentListDto> {

        val pageList = commentRepository.findRootCommentListWithWriter(pageable!!, clubAlbumSeq)

        return if(user != null) {
            PageDto(pageList.map{ comment -> ClubAlbumCommentListDto(comment, user)})
        } else {
            PageDto(pageList.map(::ClubAlbumCommentListDto))
        }
    }

    // 특정 댓글의 뎁스별 하위 댓글 조회
    @Transactional(readOnly = true)
    fun getChildCommentList(user: User?, parentCommentSeq: Long, depthLimitCnt: Long = 1): List<ClubAlbumCommentListDto> {
        val parentComment = getValidCommentBySeq(parentCommentSeq)

        val pageList: List<ClubAlbumCommentCTE> = commentRepository.findChildCommentsWithWriter(
            parentCommentSeq = parentComment.seq!!,
            startDepth = parentComment.depth + 1,
            limitDepth = parentComment.depth + depthLimitCnt
        )

        return if(user != null) {
            pageList.map{ comment -> ClubAlbumCommentListDto(comment, user) }
        } else {
            pageList.map(::ClubAlbumCommentListDto)
        }
    }

    // 댓글 등록
    @Transactional
    fun registerComment(clubSeq          : Long,
                        clubAlbumSeq     : Long,
                        parentCommentSeq : Long?,
                        user: User,
                        body: ClubAlbumCommentRegisterDto): ClubAlbumComment {

        val clubUser = clubUserService.getValidClubUser(clubSeq, user)
        if (!roleService.hasClubMemberAuth(clubUser)) {
            throw InsufficientAuthException("탈퇴한 유저는 댓글 등록을 할 수 없습니다.")
        }

        val clubAlbum= clubAlbumService.getValidClubAlbumBySeq(clubAlbumSeq)

        var parentComment: ClubAlbumComment ?= null
        var depth = 1L

        if(parentCommentSeq != null) {
            parentComment = getValidCommentBySeq(parentCommentSeq)
            depth = parentComment.depth + 1L
        }

        val comment = ClubAlbumComment(
            content    = body.content,
            clubUser   = clubUser,
            clubAlbum  = clubAlbum,
            parent     = parentComment,
            depth      = depth,
            deleteFlag = false
        )

        return commentRepository.save(comment)
    }

    // 댓글 수정
    @Transactional
    fun editComment(
            clubSeq       : Long,
            clubAlbumSeq  : Long,
            clubAlbumCommentSeq: Long,
            user: User,
            body: ClubAlbumCommentRegisterDto) {

        val clubUser = clubUserService.getValidClubUser(clubSeq, user)
        val comment  = getValidCommentBySeq(clubAlbumCommentSeq)

        if(comment.clubUser != clubUser) {
            throw OnlyWriterCanAccessException("댓글 작성자만 수정 할 수 있습니다.")
        }

        comment.content = body.content
    }

    @Transactional
    fun removeComment(clubSeq: Long, clubAlbumSeq: Long, clubAlbumCommentSeq: Long, user: User) {
        val clubUser = clubUserService.getValidClubUser(clubSeq, user)
        val comment  = getValidCommentBySeq(clubAlbumCommentSeq)

        // 관리자이거나, 작성자만 삭제 가능
        val hasManagerAuth = roleService.hasClubManagerAuth(clubUser)
        val isWriter       = clubUser == comment.clubUser

        if(!hasManagerAuth && !isWriter) {
            throw OnlyWriterCanAccessException("댓글은 작성자와 관리자만 삭제 할 수 있습니다.")
        }

        val directChildComments = commentRepository.findChildCommentsWithWriter(
            parentCommentSeq = comment.seq!!,
            startDepth       = comment.depth + 1,
            limitDepth       = comment.depth + 2
        )

        if(directChildComments.isEmpty()) {
            commentRepository.delete(comment)
        } else {
            comment.deleteFlag = true
            comment.content = if(isWriter) {
                "작성자가 삭제한 댓글입니다"
            } else {
                "관리자가 삭제한 댓글입니다"
            }
        }
    }
}
