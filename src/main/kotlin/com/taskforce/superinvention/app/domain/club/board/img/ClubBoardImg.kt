package com.taskforce.superinvention.app.domain.club.board.img

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class ClubBoardImg(

        @ManyToOne( fetch = FetchType.LAZY)
        @JoinColumn(name = "club_board_seq")
        var clubBoard: ClubBoard,

        var imgUrl : String,
        var imgName: String = "",
        var deleteFlag: Boolean

): BaseEntity()