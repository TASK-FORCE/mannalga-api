package com.taskforce.superinvention.app.domain.club.board.comment

import com.blazebit.persistence.CriteriaBuilderFactory
import com.blazebit.persistence.querydsl.BlazeJPAQuery
import com.querydsl.core.types.dsl.BooleanExpression
import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.app.domain.club.board.QClubBoard
import com.taskforce.superinvention.app.domain.user.QUser
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
interface ClubBoardCommentRepository : JpaRepository<ClubBoardComment, Long>, ClubBoardCommentRepositoryCustom {
    fun findByClubBoardIn(clubBoards: Iterable<ClubBoard>): List<ClubBoardComment>
}

interface ClubBoardCommentRepositoryCustom {
    fun findRootCommentListWithWriter(pageable: Pageable, clubBoardSeq: Long): Page<ClubBoardComment>
    fun findChildCommentsWithWriter(parentCommentSeq: Long, startDepth: Long = 2, limitDepth: Long = startDepth+1): List<ClubBoardCommentCTE>
}

@Repository
class ClubBoardCommentRepositoryImpl: ClubBoardCommentRepositoryCustom,
    QuerydslRepositorySupport(ClubBoardComment::class.java) {

    companion object {
        const val rootDepth: Long = 1
    }

    @Autowired
    lateinit var criteriaBuilderFactory: CriteriaBuilderFactory

    override fun findRootCommentListWithWriter(pageable: Pageable, clubBoardSeq: Long): Page<ClubBoardComment> {
        val user = QUser.user
        val comment = QClubBoardComment.clubBoardComment

        // @TODO N+1 issue
        val result = from(comment)
            .join(comment.clubUser.user, user)
            .where(eqSeq(comment.clubBoard, clubBoardSeq), comment.depth.eq(rootDepth))
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .fetchResults()

        return PageImpl(result.results, pageable, result.total)
    }

    override fun findChildCommentsWithWriter(parentCommentSeq: Long, startDepth: Long, limitDepth: Long): List<ClubBoardCommentCTE> {

        val user       = QUser.user
        val comment    = QClubBoardComment.clubBoardComment
        val commentCTE = QClubBoardCommentCTE.clubBoardCommentCTE

        val parentComment = QClubBoardCommentCTE("parentComment")
        val query = BlazeJPAQuery<ClubBoardCommentCTE>(super.getEntityManager(), criteriaBuilderFactory)
            .withRecursive(commentCTE, BlazeJPAQuery<ClubBoardCommentCTE>().unionAll(
                BlazeJPAQuery<ClubBoardCommentCTE>()
                    .from(comment)
                    .bind(commentCTE.seq      , comment.seq)
                    .bind(commentCTE.content  , comment.content)
                    .bind(commentCTE.clubBoard, comment.clubBoard)
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
                BlazeJPAQuery<ClubBoardCommentCTE>()
                    .from(comment)
                    .join(parentComment)
                    .on(eqSeq(comment.parent, parentComment))
                    .bind(commentCTE.seq      , comment.seq)
                    .bind(commentCTE.content  , comment.content)
                    .bind(commentCTE.clubBoard, comment.clubBoard)
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

    private fun eqSeq(commentParent: QClubBoardComment, commentCTE: QClubBoardCommentCTE): BooleanExpression {
        return commentParent.seq.eq(commentCTE.seq)
    }

    private fun eqSeq(clubBoard: QClubBoard, clubBoardSeq: Long): BooleanExpression {
        return clubBoard.seq.eq(clubBoardSeq)
    }
}
