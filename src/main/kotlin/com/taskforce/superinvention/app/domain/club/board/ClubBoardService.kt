package com.taskforce.superinvention.app.domain.club.board

import com.taskforce.superinvention.app.domain.club.board.img.ClubBoardImgService
import com.taskforce.superinvention.app.domain.club.board.like.ClubBoardLikeRepository
import com.taskforce.superinvention.app.domain.club.board.like.ClubBoardLikeRepositoryCustom
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardDto
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardRegisterBody
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardListViewDto
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardSearchOpt
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.common.exception.ResourceNotFoundException
import com.taskforce.superinvention.common.exception.auth.InsufficientAuthException
import com.taskforce.superinvention.common.exception.club.UserIsNotClubMemberException
import com.taskforce.superinvention.common.exception.club.board.ClubBoardNotFoundException
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClubBoardService(
        private val roleService: RoleService,
        private val clubBoardImgService: ClubBoardImgService,
        private val clubBoardLikeRepository: ClubBoardLikeRepository,
        private val clubBoardRepository: ClubBoardRepository,
        private val clubUserRepository: ClubUserRepository,

        @Value("\${host.static.path}")
        private var imgHost: String,
) {

    companion object {
        private val resourceNotFoundException = ResourceNotFoundException()
    }

    fun getValidClubBoardBySeq(clubBoardSeq: Long): ClubBoard {
        return clubBoardRepository.findByIdOrNull(clubBoardSeq)
            ?: throw ClubBoardNotFoundException()
    }

    /**
     * 게시판 글 목록 조회
     */
    @Transactional
    fun getClubBoardList(pageable: Pageable, category: ClubBoard.Category?, searchOpt: ClubBoardSearchOpt, clubSeq: Long): PageDto<ClubBoardListViewDto> {
        val pageRequest: Pageable = PageRequest.of(pageable.pageNumber, pageable.pageSize)

        val resultPage = clubBoardRepository.searchInList(pageRequest, category, searchOpt, clubSeq)
            .map{ clubBoard -> ClubBoardListViewDto(imgHost, clubBoard)}

        return PageDto(resultPage)
    }

    /**
     * 게시판 글 단건 조회
     */
    @Transactional
    fun getClubBoard(user: User?, boardSeq: Long, clubSeq: Long): ClubBoardDto {

        val clubBoard = clubBoardRepository.findBySeqWithWriter(boardSeq)
            ?: throw resourceNotFoundException

        val imgList = clubBoardImgService.getImageList(clubBoard) ;

        // 조회자가 좋아요를 눌렀을 경우
        val isLiked = user
            ?.let { clubUserRepository.findByClubSeqAndUser(clubSeq, user) }
            ?.let { clubUser ->  clubBoardLikeRepository.findByClubBoardAndClubUser(clubBoard, clubUser) }
            ?.let { true } ?: false

        return ClubBoardDto(imgHost, clubBoard, imgList, isLiked)
    }

    /**
     * 클럽 게시판 글 등록
     */
    @Transactional(rollbackFor = [Exception::class])
    fun registerClubBoard(user: User, clubSeq: Long, body: ClubBoardRegisterBody): ClubBoard {
        val writer: ClubUser = clubUserRepository.findByClubSeqAndUser(clubSeq, user)
                ?: throw UserIsNotClubMemberException()

        // [1] 매니저 권한 이상일 경우 에만 공지사항 가능
        if(body.category == ClubBoard.Category.NOTICE) {
            if(!roleService.hasClubManagerAuth(writer)) {
                throw InsufficientAuthException()
            }
        }

        val clubBoard = ClubBoard (
                title    = body.title,
                content  = body.content,
                club     = writer.club,
                clubUser = writer,
                category = body.category
        )

        // [2] 글 저장
        clubBoardRepository.save(clubBoard)

        // [3] 첨부 이미지가 존재 할 경우 이미지 저장
        if(body.imgList.isNotEmpty()) {
            clubBoardImgService.registerImg(clubBoard, body.imgList)
        }

        return clubBoard
    }

    @Transactional
    fun editClubBoard(user: User, clubSeq: Long, clubBoardSeq: Long, body: ClubBoardEditBody) {

        val actor: ClubUser = clubUserRepository.findByClubSeqAndUser(clubSeq, user)
            ?: throw UserIsNotClubMemberException()

        val clubBoard = clubBoardRepository.findBySeqWithWriter(clubBoardSeq)
            ?: throw ClubBoardNotFoundException()

        if(clubBoard.clubUser != actor) {
            throw InsufficientAuthException("계시글 수정은 작성자만 할 수 있습니다.", HttpStatus.FORBIDDEN)
        }

        if(body.category != null) {
            if(body.category == ClubBoard.Category.NOTICE && roleService.hasClubManagerAuth(actor)) {
                throw InsufficientAuthException("매니저, 마스터만 공지사항으로 공지사항으로 변경할 수 있습니다. ", HttpStatus.FORBIDDEN)
            }

            clubBoard.category = body.category
        }

        if(!body.title.isNullOrBlank()) {
            clubBoard.title = body.title
        }

        if(!body.content.isNullOrBlank()) {
            clubBoard.content = body.content
        }

        val clubBoardImgs = clubBoardImgService.getImageList(clubBoard)
        if(body.imgDeleteList.isNotEmpty()) {
            val deleteImgSeq: List<Long> = clubBoardImgs
                .filter { clubBoardImgDto -> body.imgDeleteList.contains(clubBoardImgDto.imgSeq) }
                .map { dto -> dto.imgSeq }

            clubBoardImgService.deleteImageBySeqIn(deleteImgSeq)
        }

        if(body.imgAddList.isNotEmpty()) {
            clubBoardImgService.registerImg(clubBoard, body.imgAddList)
        }
    }

    /**
     * 게시판 글 삭제
     */
    @Transactional
    fun deleteClubBoard(user: User, clubBoardSeq: Long) {
        val clubBoard: ClubBoard = clubBoardRepository.findBySeq(clubBoardSeq)
        val actor = clubUserRepository.findByClubAndUser(clubBoard.club, user)
            ?: throw UserIsNotClubMemberException()
        val isWriter =  clubBoard.clubUser == actor

        if(!roleService.hasClubManagerAuth(actor) && !isWriter) {
            throw InsufficientAuthException("매니저, 마스터 그리고 작성자만 삭제할 수 있습니다. ", HttpStatus.FORBIDDEN)
        }

        clubBoard.deleteFlag = true
        clubBoardImgService.deleteImageAllInClubBoard(clubBoard)
    }
}
