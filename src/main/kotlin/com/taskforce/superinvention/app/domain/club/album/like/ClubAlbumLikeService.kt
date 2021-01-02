package com.taskforce.superinvention.app.domain.club.album.like

import com.taskforce.superinvention.app.domain.club.album.ClubAlbumRepository
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.album.like.ClubAlbumLikeDto
import com.taskforce.superinvention.common.exception.club.ClubNotFoundException
import com.taskforce.superinvention.common.exception.club.UserIsNotClubMemberException
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClubAlbumLikeService(
        private val clubAlbumLikeRepository: ClubAlbumLikeRepository,
        private val clubAlbumRepository    : ClubAlbumRepository,
        private val clubUserRepository     : ClubUserRepository
) {

    @Transactional
    fun registerClubAlbumLike(user: User, clubSeq: Long, clubAlbumSeq: Long): ClubAlbumLikeDto {
        val clubUser   = clubUserRepository.findByClubSeqAndUser(clubSeq, user)
            ?: throw UserIsNotClubMemberException()

        val clubAlbum  = clubAlbumRepository.findByIdOrNull(clubAlbumSeq)
            ?: throw ClubNotFoundException()


        // 좋아요 하지 않았을 경우에만 좋아요 처리
        clubAlbumLikeRepository.findByClubAlbumAndClubUser(clubAlbum, clubUser)
            ?: clubAlbumLikeRepository.save(ClubAlbumLike(clubAlbum, clubUser))

        val likeCnt = clubAlbumLikeRepository.getClubAlbumLikeCnt(clubAlbum)

        return ClubAlbumLikeDto(
            clubSeq      = clubSeq,
            clubAlbumSeq = clubAlbumSeq,
            likeCnt      = likeCnt
        )
    }

    @Transactional
    fun removeClubAlbumLike(user: User, clubSeq: Long, clubAlbumSeq: Long): ClubAlbumLikeDto {
        val clubUser   = clubUserRepository.findByClubSeqAndUser(clubSeq, user)
            ?: throw UserIsNotClubMemberException()

        val clubAlbum  = clubAlbumRepository.findByIdOrNull(clubAlbumSeq)
            ?: throw ClubNotFoundException()

        clubAlbumLikeRepository.findByClubAlbumAndClubUser(clubAlbum, clubUser)
                ?.let((clubAlbumLikeRepository::delete))

        val likeCnt = clubAlbumLikeRepository.getClubAlbumLikeCnt(clubAlbum)

        return ClubAlbumLikeDto(
            clubSeq      = clubSeq,
            clubAlbumSeq = clubAlbumSeq,
            likeCnt      = likeCnt
        )
    }
}