package com.taskforce.superinvention.app.domain.club.album.comment

import com.querydsl.core.QueryResults
import com.querydsl.core.Tuple
import com.taskforce.superinvention.app.domain.club.user.QClubUser
import com.taskforce.superinvention.app.domain.user.QUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

interface ClubAlbumCommentRepository: JpaRepository<ClubAlbumComment, Long>,
                                      ClubAlbumCommentRepositoryCustom

interface ClubAlbumCommentRepositoryCustom {
    fun findCommentList(pageable: Pageable, clubAlbumSeq: Long): QueryResults<ClubAlbumComment>
}

@Repository
class ClubAlbumCommentRepositoryImpl: ClubAlbumCommentRepositoryCustom,
    QuerydslRepositorySupport(ClubAlbumComment::class.java) {

    override fun findCommentList(pageable: Pageable, clubAlbumSeq: Long): QueryResults<ClubAlbumComment> {
        val user = QUser.user
        val clubUser = QClubUser.clubUser
        val clubAlbumComment = QClubAlbumComment.clubAlbumComment
        val query = from(clubAlbumComment)
                .join(clubAlbumComment.clubUser.user, user).fetchJoin()
                .where(clubAlbumComment.clubAlbum.seq.eq(clubAlbumSeq))
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
        return query.fetchResults()
    }
}