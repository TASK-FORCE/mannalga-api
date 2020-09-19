package com.taskforce.superinvention.app.domain.club.board

import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardBody
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardDto
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardSearchOpt
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClubBoardService(
        private val clubBoardRepository: ClubBoardRepository,
        private val clubUserRepository: ClubUserRepository
) {
    fun getClubBoardList(pageable: Pageable, searchOpt: ClubBoardSearchOpt, clubSeq: Long): Page<ClubBoardDto> {

        val pageRequest: Pageable = PageRequest.of(pageable.pageNumber - 1, pageable.pageSize)
        val search = clubBoardRepository.search(pageRequest, searchOpt, clubSeq)

        return search.map { cb -> ClubBoardDto(cb)}
    }

    /**
     * 클럽 게시판 글 등록
     */
    @Transactional
    fun registerClubBoard(user: User, clubSeq: Long, body: ClubBoardBody) {
        val writer: ClubUser = clubUserRepository.findByClub_SeqAndUser(clubSeq, user)

        val clubBoard = ClubBoard (
                title   = body.title,
                content = body.content,
                clubUser = writer,
                topFixedFlag = false,
                deleteFlag   = false,
                notificationFlag = false,
                club = writer.club
        )
        clubBoardRepository.save(clubBoard)
    }
}