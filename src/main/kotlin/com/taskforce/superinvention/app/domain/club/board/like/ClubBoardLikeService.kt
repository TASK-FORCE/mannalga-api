package com.taskforce.superinvention.app.domain.club.board.like

import com.taskforce.superinvention.app.domain.club.board.ClubBoardRepository
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.board.like.ClubBoardLikeDto
import com.taskforce.superinvention.common.exception.auth.InsufficientAuthException
import com.taskforce.superinvention.common.exception.club.ClubNotFoundException
import com.taskforce.superinvention.common.exception.club.UserIsNotClubMemberException
import com.taskforce.superinvention.common.exception.club.board.ClubBoardNotFoundException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClubBoardLikeService(
    private val clubBoardLikeRepository: ClubBoardLikeRepository,
    private val clubBoardRepository    : ClubBoardRepository,
    private val clubUserRepository     : ClubUserRepository,
    private val roleService: RoleService
) {

    @Transactional
    fun registerClubBoardLike(user: User, clubSeq: Long, clubBoardSeq: Long): ClubBoardLikeDto {
        val clubUser  = clubUserRepository.findByClubSeqAndUser(clubSeq, user)
            ?: throw UserIsNotClubMemberException()

        if (!roleService.hasClubMemberAuth(clubUser)) {
            throw InsufficientAuthException("탈퇴한 유저는 좋아요를 할 수 없습니다.")
        }

        val clubBoard = clubBoardRepository.findByIdOrNull(clubBoardSeq)
            ?: throw ClubBoardNotFoundException()


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

        if (!roleService.hasClubMemberAuth(clubUser)) {
            throw InsufficientAuthException("탈퇴한 유저는 좋아요 취소를 할 수 없습니다.")
        }

        val clubBoard  = clubBoardRepository.findByIdOrNull(clubBoardSeq)
            ?: throw ClubBoardNotFoundException()

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