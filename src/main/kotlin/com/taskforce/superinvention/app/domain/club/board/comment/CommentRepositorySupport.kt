package com.taskforce.superinvention.app.domain.club.board.comment

import com.taskforce.superinvention.app.domain.club.board.comment.QComment.*
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository


@Repository
class CommentRepositorySupport : QuerydslRepositorySupport(Comment::class.java){
    fun findBySeq(seq: Long): Comment {
        return from(comment)
                .where(comment.seq.eq(seq))
                .fetchOne()
    }
}