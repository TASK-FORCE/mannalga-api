package com.taskforce.superinvention.app.domain.club.album.comment

import com.taskforce.superinvention.app.domain.club.album.ClubAlbumRepository
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.album.comment.ClubAlbumCommentRegisterDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.data.domain.Page
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
    fun getCommentList(pageable: Pageable, clubAlbumSeq: Long): Page<ClubAlbumComment> {
        val list = commentRepository.findCommentList(pageable, clubAlbumSeq)
        return PageImpl(list.results, pageable, list.total)
    }

    // 댓글 등록
    @Transactional
    fun registerComment(clubSeq     : Long,
                        clubAlbumSeq: Long,
                        user: User,
                        body: ClubAlbumCommentRegisterDto) {

        val clubUser = clubUserRepository.findByClubSeqAndUser(clubSeq, user)
        val clubAlbum= clubAlbumRepository.findByIdOrNull(clubAlbumSeq)

        if(clubAlbum != null && clubUser != null) {
            val clubAlbumComment = ClubAlbumComment(
                    content   = body.content,
                    clubUser  = clubUser    ,
                    clubAlbum = clubAlbum
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
            }
        }
    }

    @Transactional
    fun removeComment(clubSeq: Long, clubAlbumSeq: Long, clubAlbumCommentSeq: Long, user: User) {
        val clubUser = clubUserRepository.findByClubSeqAndUser(clubSeq, user)
        val comment = commentRepository.findByIdOrNull(clubAlbumCommentSeq)

        if(comment != null) {
            if(roleService.hasClubMemberAuth(clubSeq, user) || clubUser == comment.clubUser) {
                commentRepository.delete(comment)
            }
        }
    }
}