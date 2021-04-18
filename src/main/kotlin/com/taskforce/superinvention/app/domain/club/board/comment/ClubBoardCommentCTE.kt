package com.taskforce.superinvention.app.domain.club.board.comment

import com.blazebit.persistence.CTE
import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.album.ClubAlbum
import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@CTE
@Entity
class ClubBoardCommentCTE(

    var content: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="parent_comment_seq")
    var parent: ClubBoardComment?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="club_user_seq")
    var clubUser: ClubUser,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="club_board_seq")
    var clubBoard: ClubBoard,

    var depth: Long,

    var subCommentCnt: Long?,

    var deleteFlag: Boolean ?= false,

    ): BaseEntity()
