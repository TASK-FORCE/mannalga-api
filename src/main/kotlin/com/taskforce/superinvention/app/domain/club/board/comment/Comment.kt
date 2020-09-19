package com.taskforce.superinvention.app.domain.club.board.comment

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne

@Entity
class Comment(
        var content: String,
        @ManyToOne( fetch = FetchType.LAZY)
        var clubBoard: ClubBoard,
        @ManyToOne( fetch = FetchType.LAZY)
        var topComment: Comment,
        var deleteFlag: Boolean,
        @ManyToOne( fetch = FetchType.LAZY)
        var clubUser: ClubUser
) : BaseEntity() {
}