package com.taskforce.superinvention.app.domain.club.board

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.board.img.ClubBoardImg
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import org.hibernate.annotations.Formula
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

    @Enumerated(EnumType.STRING)
    var category: Category,

    var deleteFlag: Boolean ?= false,

): BaseEntity() {

        @Formula("(select count(*) from club_board_comment cbc where cbc.club_board_seq = seq)")
        var boardCommentCnt: Long ?= null

        @Formula("(select count(*) from club_board_like cbl where cbl.club_board_seq = seq)")
        var boardLikeCnt: Long? = null

        @OneToMany
        @JoinColumn(name = "club_board_seq")
        var boardImgs: List<ClubBoardImg> = emptyList()

        enum class Category(
            val detail   : String,
            val label    : String,
            val priority : Int,
        ) {
            NORMAL("일반 게시글", Category.NORMAL, 0),
            NOTICE("공지 사항" ,  Category.NOTICE, 1);

            companion object {
                private const val NORMAL = "NORMAL"
                private const val NOTICE = "NOTICE"

                private val lookup: Map<String, Category> = values().associateBy(Category::label)
                fun fromCategoryLabel(label:  String): Category? = lookup[label]
            }
        }
}