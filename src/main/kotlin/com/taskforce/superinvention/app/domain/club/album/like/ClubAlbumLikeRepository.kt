package com.taskforce.superinvention.app.domain.club.album.like

import com.taskforce.superinvention.app.domain.club.album.ClubAlbum
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import org.springframework.data.jpa.repository.JpaRepository

interface ClubAlbumLikeRepository: JpaRepository<ClubAlbumLike, Long> {
    fun findByClubAlbumAndClubUser(clubAlbum: ClubAlbum, clubUser: ClubUser): ClubAlbumLike?
}
