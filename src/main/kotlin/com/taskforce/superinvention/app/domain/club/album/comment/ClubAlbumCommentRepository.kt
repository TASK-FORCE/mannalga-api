package com.taskforce.superinvention.app.domain.club.album.comment

import com.querydsl.core.QueryResults
import com.querydsl.core.types.dsl.BooleanExpression
import com.taskforce.superinvention.app.domain.club.QClub
import com.taskforce.superinvention.app.domain.club.album.QClubAlbum
import com.taskforce.superinvention.app.domain.club.user.QClubUser
import com.taskforce.superinvention.app.domain.user.QUser
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

interface ClubAlbumCommentRepository: JpaRepository<ClubAlbumComment, Long>,
                                      ClubAlbumCommentRepositoryCustom

interface ClubAlbumCommentRepositoryCustom {
    fun findCommentListWithWriter(pageable: Pageable, clubAlbumSeq: Long): QueryResults<ClubAlbumComment>
}

@Repository
class ClubAlbumCommentRepositoryImpl: ClubAlbumCommentRepositoryCustom,
    QuerydslRepositorySupport(ClubAlbumComment::class.java) {

    override fun findCommentListWithWriter(pageable: Pageable, clubAlbumSeq: Long): QueryResults<ClubAlbumComment> {
        val user = QUser.user
        val clubUser = QClubUser.clubUser
        val clubAlbumComment = QClubAlbumComment.clubAlbumComment

        // @TODO N+1 issue
        val query = from(clubAlbumComment)
                .join(clubAlbumComment.clubUser.user, user)
                .where(clubAlbumComment.clubAlbum.seq.eq(clubAlbumSeq))
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())

        return query.fetchResults()
    }

    private fun eqSeq(clubAlbum: QClubAlbum, clubAlbumSeq: Long): BooleanExpression {
        return clubAlbum.seq.eq(clubAlbumSeq)
    }
}