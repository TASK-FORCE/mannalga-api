package com.taskforce.superinvention.app.domain.club.board.like

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import javax.persistence.*

@Entity
class ClubBoardLike(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_board_seq")
    val clubBoard: ClubBoard,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "club_user_seq")
    val clubUser: ClubUser

): BaseEntity()