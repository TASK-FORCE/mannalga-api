package com.taskforce.superinvention.app.domain.club.board.comment

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.ClubUser
import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class Comment(
        var content: String,
        @ManyToOne
        var clubBoard: ClubBoard,
        @ManyToOne
        var topComment: Comment,
        var deleteFlag: Boolean,
        @ManyToOne
        var clubUser: ClubUser
) : BaseEntity() {
}