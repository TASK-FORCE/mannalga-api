package com.taskforce.superinvention.app.domain.board

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.QClub
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class ClubBoardRepositorySupport : QuerydslRepositorySupport(ClubBoard::class.java) {

    fun findBySeq(seq: Long): ClubBoard {
        return from(QClubBoard.clubBoard)
                .where(QClubBoard.clubBoard.seq.eq(seq))
                .fetchOne()
    }

}