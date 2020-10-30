package com.taskforce.superinvention.app.domain.club.board

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.board.img.ClubBoardImg
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import javax.persistence.*

@Entity
class ClubBoard(
        var title: String,
        var content: String,

        @ManyToOne( fetch = FetchType.LAZY)
        @JoinColumn(name = "club_user_seq")
        var clubUser: ClubUser,

        @ManyToOne( fetch = FetchType.LAZY)
        @JoinColumn(name = "club_seq")
        var club: Club,

        @OneToOne( fetch = FetchType.LAZY)
        @JoinColumn(name = "title_img_seq")
        var titleImg: ClubBoardImg ?= null,

        var topFixedFlag: Boolean,
        var deleteFlag: Boolean,
        var notificationFlag: Boolean

): BaseEntity() {

        @OneToMany(mappedBy = "clubBoard")
        lateinit var boardImgs: MutableList<ClubBoardImg>
}