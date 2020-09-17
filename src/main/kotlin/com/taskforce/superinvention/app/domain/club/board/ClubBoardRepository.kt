package com.taskforce.superinvention.app.domain.club.board

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

interface ClubBoardRepository : JpaRepository<ClubBoard, Long> {
    fun findBySeq(seq: Long): ClubBoard
}

@Repository
class ClubBoardRepositorySupport : QuerydslRepositorySupport(ClubBoard::class.java) {
}