package com.taskforce.superinvention.app.domain.club.board

import com.querydsl.jpa.JPQLQuery
import com.taskforce.superinvention.app.domain.club.user.QClubUser
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
     fun search(pageable: Pageable, searchOpt: ClubBoardSearchOpt, clubSeq: Long): Page<ClubBoard>
}

@Repository
class ClubBoardRepositoryImpl : ClubBoardCustom, QuerydslRepositorySupport(ClubBoard::class.java)  {

    override fun search(pageable: Pageable, searchOpt: ClubBoardSearchOpt, clubSeq: Long): Page<ClubBoard> {
        val clubBoard = QClubBoard.clubBoard
        val user       = QUser.user

        val jpqlQuery = from(clubBoard)
                .innerJoin(clubBoard.clubUser.user, user)
                .where(clubBoard.club.seq.eq(clubSeq))

        // 제목 검색
        if(searchOpt.title!!.isNotBlank()) {
            jpqlQuery.where(clubBoard.title.likeIgnoreCase("%${searchOpt.title}"))
        }

        // 내용 검색
        if(searchOpt.content!!.isNotBlank()) {
            jpqlQuery.where(clubBoard.content.likeIgnoreCase("%${searchOpt.content}"))
        }

        // 삭제된 글 필터링
        jpqlQuery.where(clubBoard.deleteFlag.isFalse)

        val clubBoardList = querydsl!!.applyPagination(pageable, jpqlQuery).fetch()
        return PageImpl(clubBoardList, pageable, jpqlQuery.fetchCount())
    }
}