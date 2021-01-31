package com.taskforce.superinvention.app.domain.club.board

import com.querydsl.core.types.dsl.BooleanExpression
import com.taskforce.superinvention.app.domain.club.QClub
import com.taskforce.superinvention.app.domain.club.board.img.QClubBoardImg
import com.taskforce.superinvention.app.domain.user.QUser
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardSearchOpt
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

interface ClubBoardRepository : JpaRepository<ClubBoard, Long>, ClubBoardCustom {
    fun findBySeq(seq: Long): ClubBoard
}

interface ClubBoardCustom {
     fun searchInList(pageable: Pageable, searchOpt: ClubBoardSearchOpt, clubSeq: Long): Page<ClubBoard>
}

@Repository
class ClubBoardRepositoryImpl : ClubBoardCustom, QuerydslRepositorySupport(ClubBoard::class.java)  {

    // 클럽 게시판 리스트 조회
    override fun searchInList(pageable: Pageable, searchOpt: ClubBoardSearchOpt, clubSeq: Long): Page<ClubBoard> {
        val clubBoard   : QClubBoard    = QClubBoard.clubBoard
        val clubBoardImg: QClubBoardImg = QClubBoardImg.clubBoardImg
        val user: QUser = QUser.user

        val query = from(clubBoard)
                .leftJoin(clubBoard.clubUser.user, user)
                .leftJoin(clubBoard.boardImgs, clubBoardImg)

        // 제목 검색
        if(searchOpt.title.isNotBlank()) {
            query.where(clubBoard.title.likeIgnoreCase("${searchOpt.title}%"))
        }

        // 내용 검색
        if(searchOpt.content.isNotBlank()) {
            query.where(clubBoard.content.likeIgnoreCase("${searchOpt.content}%"))
        }

        // 삭제된 글 필터링
        query
            .where(clubBoard.deleteFlag.isFalse, eqSeq(clubBoard.club, clubSeq))
            .groupBy(clubBoard.seq)

        val fetchResult = query.fetchResults();

        return PageImpl(fetchResult.results, pageable, fetchResult.total)
    }

    private fun eqSeq(club: QClub, clubSeq: Long): BooleanExpression {
        return club.seq.eq(clubSeq)
    }
}