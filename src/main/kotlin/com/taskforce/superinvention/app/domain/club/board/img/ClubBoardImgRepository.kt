package com.taskforce.superinvention.app.domain.club.board.img

import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.app.domain.club.board.comment.ClubBoardComment
import com.taskforce.superinvention.app.domain.club.board.comment.ClubBoardCommentRepositoryCustom
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

interface ClubBoardImgRepository : JpaRepository<ClubBoardImg, Long>, ClubBoardImgRepositoryCustom {
    fun findByClubBoard(clubBoard: ClubBoard): List<ClubBoardImg>
    fun findBySeqIn(clubBoardSeqList: List<Long>): List<ClubBoardImg>
}

interface ClubBoardImgRepositoryCustom {
    fun findByClubBoardOrderByOrderAsc(clubBoard: ClubBoard): List<ClubBoardImg>
}

@Repository
class ClubBoardImgRepositoryImpl: ClubBoardImgRepositoryCustom,
    QuerydslRepositorySupport(ClubBoardImg::class.java) {

    override fun findByClubBoardOrderByOrderAsc(clubBoard: ClubBoard): List<ClubBoardImg> {
        val clubBoardImg = QClubBoardImg.clubBoardImg

        return from(clubBoardImg)
            .where(
                clubBoardImg.clubBoard.eq(clubBoard),
                clubBoardImg.deleteFlag.isFalse,
            )
            .orderBy(clubBoardImg.displayOrder.asc())
            .fetch()
    }
}
