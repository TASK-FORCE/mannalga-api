package com.taskforce.superinvention.app.domain.club.album.like

import com.taskforce.superinvention.app.domain.club.album.ClubAlbumRepository
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.user.User
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
    fun registerClubAlbumLike(user: User, clubSeq: Long, clubAlbumSeq: Long) {
        val clubUser   = clubUserRepository.findByClubSeqAndUser(clubSeq, user)
        val clubAlbum = clubAlbumRepository.findByIdOrNull(clubAlbumSeq)

        if(clubUser != null && clubAlbum != null) {

            // 좋아요 하지 않았을 경우에만 좋아요 처리
            clubAlbumLikeRepository.findByClubAlbumAndClubUser(clubAlbum, clubUser)
                    ?: run { clubAlbumLikeRepository.save(ClubAlbumLike(clubAlbum, clubUser)) }
        }
    }

    @Transactional
    fun removeClubAlbumLike(user: User, clubSeq: Long, clubAlbumSeq: Long) {
        val clubUser   = clubUserRepository.findByClubSeqAndUser(clubSeq, user)
        val clubAlbum = clubAlbumRepository.findByIdOrNull(clubAlbumSeq)

        if(clubUser != null && clubAlbum != null) {
            clubAlbumLikeRepository.findByClubAlbumAndClubUser(clubAlbum, clubUser)
                    ?.let((clubAlbumLikeRepository::delete))
        }
    }
}