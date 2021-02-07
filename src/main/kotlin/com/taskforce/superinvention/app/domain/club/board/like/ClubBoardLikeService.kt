package com.taskforce.superinvention.app.domain.club.board.like

import com.taskforce.superinvention.app.domain.club.board.ClubBoardRepository
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.board.like.ClubBoardLikeDto
import com.taskforce.superinvention.common.exception.club.ClubNotFoundException
import com.taskforce.superinvention.common.exception.club.UserIsNotClubMemberException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClubBoardLikeService(
    private val clubBoardLikeRepository: ClubBoardLikeRepository,
    private val clubBoardRepository    : ClubBoardRepository,
    private val clubUserRepository     : ClubUserRepository
) {

    @Transactional
    fun registerClubBoardLike(user: User, clubSeq: Long, clubBoardSeq: Long): ClubBoardLikeDto {
        val clubUser  = clubUserRepository.findByClubSeqAndUser(clubSeq, user)
            ?: throw UserIsNotClubMemberException()

        val clubBoard = clubBoardRepository.findByIdOrNull(clubBoardSeq)
            ?: throw ClubNotFoundException()


        // 좋아요 하지 않았을 경우에만 좋아요 처리
        clubBoardLikeRepository.findByClubBoardAndClubUser(clubBoard, clubUser)
            ?: clubBoardLikeRepository.save(ClubBoardLike(clubBoard, clubUser))

        val likeCnt = clubBoardLikeRepository.getClubBoardLikeCnt(clubBoard)

        return ClubBoardLikeDto(
            clubSeq      = clubSeq,
            clubBoardSeq = clubBoardSeq,
            likeCnt      = likeCnt
        )
    }

    @Transactional
    fun removeClubBoardLike(user: User, clubSeq: Long, clubBoardSeq: Long): ClubBoardLikeDto {
        val clubUser   = clubUserRepository.findByClubSeqAndUser(clubSeq, user)
            ?: throw UserIsNotClubMemberException()

        val clubBoard  = clubBoardRepository.findByIdOrNull(clubBoardSeq)
            ?: throw ClubNotFoundException()

        clubBoardLikeRepository.findByClubBoardAndClubUser(clubBoard, clubUser)
                ?.let(clubBoardLikeRepository::delete)

        val likeCnt = clubBoardLikeRepository.getClubBoardLikeCnt(clubBoard)

        return ClubBoardLikeDto(
            clubSeq      = clubSeq,
            clubBoardSeq = clubBoardSeq,
            likeCnt      = likeCnt
        )
    }
}