package com.taskforce.superinvention.app.domain.club.board

import com.querydsl.core.Tuple
import com.taskforce.superinvention.app.domain.club.board.img.QClubBoardImg
import com.taskforce.superinvention.app.domain.user.QUser
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardPreviewDto
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardSearchOpt
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import java.time.format.DateTimeFormatter

interface ClubBoardRepository : JpaRepository<ClubBoard, Long>, ClubBoardCustom {
    fun findBySeq(seq: Long): ClubBoard
}

interface ClubBoardCustom {
     fun searchInList(pageable: Pageable, searchOpt: ClubBoardSearchOpt, clubSeq: Long): Page<ClubBoardPreviewDto>
}

@Repository
class ClubBoardRepositoryImpl : ClubBoardCustom, QuerydslRepositorySupport(ClubBoard::class.java)  {

    // 클럽 게시판 리스트 조회
    override fun searchInList(pageable: Pageable, searchOpt: ClubBoardSearchOpt, clubSeq: Long): Page<ClubBoardPreviewDto> {
        val clubBoard   : QClubBoard    = QClubBoard.clubBoard
        val clubBoardImg: QClubBoardImg = QClubBoardImg.clubBoardImg
        val user: QUser = QUser.user

        val jpqlQuery = from(clubBoard)
                .select(
                    clubBoard.seq,
                    clubBoard.clubUser.seq,
                    clubBoard.title,
                    clubBoard.clubUser.user.userName,
                    clubBoard.createdAt,
                    clubBoard.titleImg.imgUrl,
                    clubBoardImg.count(),
                    clubBoard.topFixedFlag,
                    clubBoard.notificationFlag
                )
                .leftJoin(clubBoard.clubUser.user, user)
                .leftJoin(clubBoard.titleImg, clubBoardImg)
                .leftJoin(clubBoard.boardImgs, clubBoardImg)

        // 제목 검색
        if(searchOpt.title.isNotBlank()) {
            jpqlQuery.where(clubBoard.title.likeIgnoreCase("%${searchOpt.title}%"))
        }

        // 내용 검색
        if(searchOpt.content.isNotBlank()) {
            jpqlQuery.where(clubBoard.content.likeIgnoreCase("%${searchOpt.content}%"))
        }

        // 삭제된 글 필터링
        jpqlQuery.where(clubBoard.deleteFlag.isFalse)
                 .where(clubBoard.club.seq.eq(clubSeq))

        jpqlQuery.groupBy(clubBoard.seq)

        // 조회한 결과 매핑
        val results: List<Tuple> = querydsl!!.applyPagination(pageable, jpqlQuery).fetch()
        val clubBoardList = results.map { tuple ->
            ClubBoardPreviewDto(
                    clubBoardSeq = tuple.get(clubBoard.seq)!!,
                    clubUserSeq  = tuple.get(clubBoard.clubUser.seq)!!,
                    title        = tuple.get(clubBoard.title) ?: "",
                    userName     = tuple.get(clubBoard.clubUser.user.userName) ?: "",
                    createdAt    = tuple.get(clubBoard.createdAt)!!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    titleImgUrl  = tuple.get(clubBoard.titleImg.imgUrl) ?: "",
                    photoCnt     = tuple.get(clubBoardImg.count()) ?: 0,
                    topFixedFlag = tuple.get(clubBoard.topFixedFlag) ?: false,
                    notificationFlag = tuple.get(clubBoard.notificationFlag) ?: false
            )
        }

        return PageImpl(clubBoardList, pageable, jpqlQuery.fetchCount())
    }
}