package com.taskforce.superinvention.app.domain.club.board.like

import com.querydsl.core.types.dsl.BooleanExpression
import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.app.domain.club.board.QClubBoard
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

interface ClubBoardLikeRepository: JpaRepository<ClubBoardLike, Long>, ClubBoardLikeRepositoryCustom {
    fun findByClubBoardAndClubUser(board: ClubBoard, clubUser: ClubUser): ClubBoardLike?
}

interface ClubBoardLikeRepositoryCustom {
    fun getClubBoardLikeCnt(clubBoard: ClubBoard): Long
}

@Repository
class ClubBoardLikeRepositoryImpl: ClubBoardLikeRepositoryCustom,
    QuerydslRepositorySupport(ClubBoard::class.java){

    override fun getClubBoardLikeCnt(clubBoard: ClubBoard): Long {
        val clubBoardLike = QClubBoardLike.clubBoardLike

        val query = from(clubBoardLike)
            .where(eqSeq(clubBoardLike.clubBoard, clubBoard))

        return query.fetchCount()
    }

    private fun eqSeq(clubBoard1: QClubBoard, clubBoard2: ClubBoard): BooleanExpression
            = clubBoard1.seq.eq(clubBoard2.seq)
}