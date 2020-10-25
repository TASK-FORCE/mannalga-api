package com.taskforce.superinvention.app.domain.club.board

import com.taskforce.superinvention.app.domain.club.board.img.ClubBoardImgService
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardBody
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardPreviewDto
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardSearchOpt
import com.taskforce.superinvention.common.exception.auth.InsufficientAuthException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClubBoardService(
        private val roleService: RoleService,
        private val clubBoardImgService: ClubBoardImgService,
        private val clubBoardRepository: ClubBoardRepository,
        private val clubUserRepository: ClubUserRepository
) {

    /**
     * 게시판 글 목록 조회
     */
    @Transactional
    fun getClubBoardList(pageable: Pageable, searchOpt: ClubBoardSearchOpt, clubSeq: Long): Page<ClubBoardPreviewDto> {

        val pageRequest: Pageable = PageRequest.of(pageable.pageNumber, pageable.pageSize)
        return clubBoardRepository.searchInList(pageRequest, searchOpt, clubSeq)
    }

    /**
     * 클럽 게시판 글 등록
     */
    @Transactional(rollbackFor = [Exception::class])
    fun registerClubBoard(user: User, clubSeq: Long, body: ClubBoardBody): ClubBoard {
        val writer: ClubUser = clubUserRepository.findByClubSeqAndUser(clubSeq, user)

        var topFixable = false

        // [1] 매니저 권한 이상일 경우 상단 고정, 알림 설정 가능
        if(roleService.hasClubManagerAuth(writer)) {
            topFixable = body.isTopFixed
        }

        val clubBoard = ClubBoard (
                title    = body.title,
                content  = body.content,
                club     = writer.club,
                clubUser = writer,
                topFixedFlag     = topFixable,
                notificationFlag = body.isNotifiable,
                deleteFlag = false
        )

        // [2] 글 저장
        clubBoardRepository.save(clubBoard)

        // [3] 첨부 이미지가 존재 할 경우 처리
        if(body.imgList.isNotEmpty()) {
            val imgList = clubBoardImgService.registerImg(clubBoard, body.imgList)

            clubBoard.titleImg = imgList[0]
        }

        return clubBoard
    }

    @Transactional
    fun editClubBoard(user: User, clubBoardSeq: Long, body: ClubBoardBody) {

        // [1] 기존 이미지 전부 삭제
    }

    /**
     * 게시판 글 삭제
     */
    @Transactional
    fun deleteClubBoard(user: User, clubBoardSeq: Long) {
        val clubBoard: ClubBoard = clubBoardRepository.findBySeq(clubBoardSeq)
        val clubUser = clubBoard.clubUser
        val isWriter =  clubBoard.clubUser.user == user

        if(!roleService.hasClubManagerAuth(clubUser) && !isWriter) {
            throw InsufficientAuthException("충분한 권한이 없습니다.", HttpStatus.FORBIDDEN)
        }

        clubBoard.deleteFlag = false;
        clubBoardImgService.deleteImages(clubBoard)
    }
}