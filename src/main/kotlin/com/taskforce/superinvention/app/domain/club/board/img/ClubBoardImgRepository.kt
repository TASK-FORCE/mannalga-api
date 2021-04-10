package com.taskforce.superinvention.app.domain.club.board.img

import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import org.springframework.data.jpa.repository.JpaRepository

interface ClubBoardImgRepository : JpaRepository<ClubBoardImg, Long> {
    fun findByClubBoard(clubBoard: ClubBoard): List<ClubBoardImg>
    fun findByClubBoardOrderByCreatedAtAsc(clubBoard: ClubBoard): List<ClubBoardImg>
    fun findBySeqIn(clubBoardSeqList: List<Long>): List<ClubBoardImg>
}
