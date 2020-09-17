package com.taskforce.superinvention.app.domain.club.board

import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.user.user.User
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardDto
import org.springframework.stereotype.Service

@Service
class ClubBoardService(
        private val clubBoardRepository: ClubBoardRepository,
        private val clubUserRepository: ClubUserRepository
) {
    fun registerClubBoard(user: User, clubSeq: Long, body: ClubBoardDto) {
        val writer: ClubUser = clubUserRepository.findByClub_SeqAndUser(clubSeq, user)

        val clubBoard = ClubBoard (
                title   = body.title,
                content = body.content,
                clubUser = writer,
                topFixedFlag = false,
                deleteFlag   = false,
                notificationFlag = false
        )

        clubBoardRepository.save(clubBoard)
    }
}