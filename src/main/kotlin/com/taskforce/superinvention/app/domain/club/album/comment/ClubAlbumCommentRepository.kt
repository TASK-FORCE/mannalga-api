package com.taskforce.superinvention.app.domain.club.album.comment

import com.blazebit.persistence.CriteriaBuilderFactory
import com.blazebit.persistence.querydsl.BlazeJPAQuery
import com.querydsl.core.types.dsl.BooleanExpression
import com.taskforce.superinvention.app.domain.club.album.QClubAlbum
import com.taskforce.superinvention.app.domain.user.QUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

interface ClubAlbumCommentRepository: JpaRepository<ClubAlbumComment, Long>,
                                      ClubAlbumCommentRepositoryCustom

interface ClubAlbumCommentRepositoryCustom {
    fun findRootCommentListWithWriter(pageable: Pageable, clubAlbumSeq: Long): Page<ClubAlbumComment>
    fun findChildCommentsWithWriter(parentCommentSeq: Long, startDepth: Long = 2, limitDepth: Long = startDepth+1): List<ClubAlbumCommentCTE>
}

@Repository
class ClubAlbumCommentRepositoryImpl: ClubAlbumCommentRepositoryCustom,
    QuerydslRepositorySupport(ClubAlbumComment::class.java) {

    companion object {
        const val rootDepth: Long = 1
    }

    @Autowired
    lateinit var criteriaBuilderFactory: CriteriaBuilderFactory

    override fun findRootCommentListWithWriter(pageable: Pageable, clubAlbumSeq: Long): Page<ClubAlbumComment> {
        val user = QUser.user
        val comment = QClubAlbumComment.clubAlbumComment

        // @TODO N+1 issue
        val result = from(comment)
                .join(comment.clubUser.user, user)
                .where(eqSeq(comment.clubAlbum, clubAlbumSeq), comment.depth.eq(rootDepth))
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetchResults()

        return PageImpl(result.results, pageable, result.total)
    }

    override fun findChildCommentsWithWriter(parentCommentSeq: Long, startDepth: Long, limitDepth: Long): List<ClubAlbumCommentCTE> {

        val user       = QUser.user
        val comment    = QClubAlbumComment.clubAlbumComment
        val commentCTE = QClubAlbumCommentCTE.clubAlbumCommentCTE

        val parentComment = QClubAlbumCommentCTE("parentComment")
        val query = BlazeJPAQuery<ClubAlbumCommentCTE>(super.getEntityManager(), criteriaBuilderFactory)
            .withRecursive(commentCTE, BlazeJPAQuery<ClubAlbumCommentCTE>().unionAll(
                BlazeJPAQuery<ClubAlbumCommentCTE>()
                    .from(comment)
                    .bind(commentCTE.seq      , comment.seq)
                    .bind(commentCTE.content  , comment.content)
                    .bind(commentCTE.clubAlbum, comment.clubAlbum)
                    .bind(commentCTE.clubUser , comment.clubUser)
                    .bind(commentCTE.createdAt, comment.createdAt)
                    .bind(commentCTE.updatedAt, comment.updatedAt)
                    .bind(commentCTE.parent, comment.parent)
                    .bind(commentCTE.depth , comment.depth)
                    .bind(commentCTE.subCommentCnt, comment.subCommentCnt)
                    .where(
                        comment.parent.seq.eq(parentCommentSeq),
                        comment.depth.eq(startDepth)
                    ),
                BlazeJPAQuery<ClubAlbumCommentCTE>()
                    .from(comment)
                    .join(parentComment)
                        .on(eqSeq(comment.parent, parentComment))
                    .bind(commentCTE.seq      , comment.seq)
                    .bind(commentCTE.content  , comment.content)
                    .bind(commentCTE.clubAlbum, comment.clubAlbum)
                    .bind(commentCTE.clubUser , comment.clubUser)
                    .bind(commentCTE.createdAt, comment.createdAt)
                    .bind(commentCTE.updatedAt, comment.updatedAt)
                    .bind(commentCTE.parent, comment.parent)
                    .bind(commentCTE.depth , comment.depth)
                    .bind(commentCTE.subCommentCnt, comment.subCommentCnt)
                    .where(comment.depth.loe(limitDepth))
            ))
            .select(commentCTE)
            .from(commentCTE)
            .join(commentCTE.clubUser.user, user)
            .orderBy(commentCTE.seq.asc(), commentCTE.createdAt.asc())
            .fetch()

        return query
    }

    private fun eqSeq(commentParent: QClubAlbumComment, commentCTE: QClubAlbumCommentCTE): BooleanExpression {
        return commentParent.seq.eq(commentCTE.seq)
    }

    private fun eqSeq(clubAlbum: QClubAlbum, clubAlbumSeq: Long): BooleanExpression {
        return clubAlbum.seq.eq(clubAlbumSeq)
    }
}
