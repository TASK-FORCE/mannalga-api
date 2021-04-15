package com.taskforce.superinvention.app.domain.club.board

import com.querydsl.core.types.dsl.BooleanExpression
import com.taskforce.superinvention.app.domain.club.QClub
import com.taskforce.superinvention.app.domain.club.board.img.QClubBoardImg
import com.taskforce.superinvention.app.domain.club.user.QClubUser
import com.taskforce.superinvention.app.domain.user.QUser
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardSearchOpt
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
interface ClubBoardRepository : JpaRepository<ClubBoard, Long>, ClubBoardCustom {
    fun findBySeq(seq: Long): ClubBoard
}

interface ClubBoardCustom {
     fun searchInList(pageable: Pageable, category: ClubBoard.Category?, searchOpt: ClubBoardSearchOpt, clubSeq: Long): Page<ClubBoard>
     fun findBySeqWithWriter(clubBoardSeq: Long): ClubBoard?
}

@Repository
class ClubBoardRepositoryImpl : ClubBoardCustom,
    QuerydslRepositorySupport(ClubBoard::class.java)  {

    // 클럽 게시판 리스트 조회
    override fun searchInList(
        pageable: Pageable,
        category: ClubBoard.Category?,
        searchOpt: ClubBoardSearchOpt,
        clubSeq: Long
    ): Page<ClubBoard> {

        val clubBoard    = QClubBoard.clubBoard
        val clubBoardImg = QClubBoardImg.clubBoardImg
        val clubUser     = QClubUser.clubUser
        val user         = QUser.user

        val query = from(clubBoard)
                .join(clubBoard.clubUser, clubUser)
                .join(clubBoard.clubUser.user, user)
                .leftJoin(clubBoard.boardImgs, clubBoardImg).fetchJoin()

        // 제목 검색
        if(searchOpt.title.isNotBlank()) {
            query.where(clubBoard.title.likeIgnoreCase("${searchOpt.title}%"))
        }

        // 내용 검색
        if(searchOpt.content.isNotBlank()) {
            query.where(clubBoard.content.likeIgnoreCase("${searchOpt.content}%"))
        }

        if(category != null) {
            query.where(clubBoard.category.eq(category))
        }

        // 삭제된 글 필터링
        query.where(
            eqSeq(clubBoard.club, clubSeq),
            clubBoard.deleteFlag.isFalse
        ).groupBy(clubBoard.seq)
        .orderBy(clubBoard.createdAt.desc())

        if (pageable != Pageable.unpaged()) {
            query.offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
        }

        val fetchResult = query.fetchResults()

        return PageImpl(fetchResult.results, pageable, fetchResult.total)
    }

    override fun findBySeqWithWriter(clubBoardSeq: Long): ClubBoard? {
        val clubBoard   : QClubBoard    = QClubBoard.clubBoard
        val user: QUser = QUser.user
        val clubUser     = QClubUser.clubUser

        val query = from(clubBoard)
            .join(clubBoard.clubUser, clubUser)
            .join(clubBoard.clubUser.user, user)
            .where(eqSeq(clubBoard, clubBoardSeq))

        return query.fetchFirst()
    }

    private fun eqSeq(club: QClubBoard, clubBoardSeq: Long): BooleanExpression {
        return club.seq.eq(clubBoardSeq)
    }

    private fun eqSeq(club: QClub, clubSeq: Long): BooleanExpression {
        return club.seq.eq(clubSeq)
    }
}
