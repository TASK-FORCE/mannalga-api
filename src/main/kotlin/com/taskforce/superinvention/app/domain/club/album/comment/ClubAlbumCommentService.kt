package com.taskforce.superinvention.app.domain.club.album.comment

import com.taskforce.superinvention.app.domain.club.album.ClubAlbumRepository
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.album.comment.ClubAlbumCommentListDto
import com.taskforce.superinvention.app.web.dto.club.album.comment.ClubAlbumCommentRegisterDto
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.common.exception.ResourceNotFoundException
import com.taskforce.superinvention.common.exception.auth.OnlyWriterCanAccessException
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClubAlbumCommentService(
        private val roleService: RoleService,
        private val clubAlbumRepository: ClubAlbumRepository,
        private val clubUserRepository : ClubUserRepository ,
        private val commentRepository  : ClubAlbumCommentRepository
) {

    @Transactional(readOnly = true)
    fun getCommentList(user: User?, pageable: Pageable?, clubAlbumSeq: Long): PageDto<ClubAlbumCommentListDto> {

        val list = commentRepository.findCommentListWithWriter(pageable!!, clubAlbumSeq)

        val result = if(user != null) {
            list.results.map{ comment -> ClubAlbumCommentListDto(comment, user) }
        } else {
            list.results.map(::ClubAlbumCommentListDto)
        }

        val resultPage = PageImpl(result, pageable, list.total)
        return PageDto(resultPage)
    }

    // 댓글 등록
    @Transactional
    fun registerComment(clubSeq          : Long,
                        clubAlbumSeq     : Long,
                        parentCommentSeq :  Long?,
                        user: User,
                        body: ClubAlbumCommentRegisterDto) {

        val clubUser = clubUserRepository.findByClubSeqAndUser(clubSeq, user)
        val clubAlbum= clubAlbumRepository.findByIdOrNull(clubAlbumSeq)
        val parentCommentRef = if(parentCommentSeq != null) {
            commentRepository.getOne(parentCommentSeq)
        } else {
            null
        }

        if(clubAlbum != null && clubUser != null) {
            val clubAlbumComment = ClubAlbumComment(
                    content   = body.content,
                    clubUser  = clubUser    ,
                    clubAlbum = clubAlbum   ,
                    parentComment = parentCommentRef
            )
            commentRepository.save(clubAlbumComment)
        }
    }

    // 댓글 수정
    @Transactional
    fun editComment(
            clubSeq       : Long,
            clubAlbumSeq  : Long,
            clubAlbumCommentSeq: Long,
            user: User,
            body: ClubAlbumCommentRegisterDto) {

        val clubUser = clubUserRepository.findByClubSeqAndUser(clubSeq, user)
        val comment = commentRepository.findByIdOrNull(clubAlbumCommentSeq)

        if(comment != null) {
            if(comment.clubUser == clubUser) {
                comment.content = body.content
                return
            }
            throw OnlyWriterCanAccessException("댓글 작성자만 수정 할 수 있습니다.")
        }
        throw ResourceNotFoundException()
    }

    @Transactional
    fun removeComment(clubSeq: Long, clubAlbumSeq: Long, clubAlbumCommentSeq: Long, user: User) {
        val clubUser = clubUserRepository.findByClubSeqAndUser(clubSeq, user)
        val comment = commentRepository.findByIdOrNull(clubAlbumCommentSeq)

        if(comment != null && clubUser != null) {

            // 관리자이거나, 작성자만 삭제 가능
            if(roleService.hasClubManagerAuth(clubUser) || clubUser == comment.clubUser) {
                commentRepository.delete(comment)
                return
            }
            throw OnlyWriterCanAccessException("댓글 작성자와 관리자만 삭제 할 수 있습니다.")
        }
        throw ResourceNotFoundException()
    }
}