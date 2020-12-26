package com.taskforce.superinvention.app.domain.club.album.like

import com.querydsl.core.types.Predicate
import com.taskforce.superinvention.app.domain.club.album.ClubAlbum
import com.taskforce.superinvention.app.domain.club.album.QClubAlbum
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

interface ClubAlbumLikeRepository: JpaRepository<ClubAlbumLike, Long>, ClubAlbumLikeCustom {

    fun findByClubAlbumAndClubUser(clubAlbum: ClubAlbum, clubUser: ClubUser): ClubAlbumLike?
}

interface ClubAlbumLikeCustom {
    fun getClubAlbumLikeCnt(clubAlbum: ClubAlbum): Long
}

@Repository
class ClubAlbumLikeRepositoryImpl: ClubAlbumLikeCustom,
    QuerydslRepositorySupport(ClubAlbumLike::class.java) {

    override fun getClubAlbumLikeCnt(clubAlbum: ClubAlbum): Long {
        val clubAlbumLike = QClubAlbumLike.clubAlbumLike

        val query = from(clubAlbumLike)
            .where(eqSeq(clubAlbumLike.clubAlbum, clubAlbum))

        return query.fetchCount()
    }


    private fun eqSeq(clubAlbum: QClubAlbum, targetClubAlbum: ClubAlbum): Predicate {
        return clubAlbum.seq.eq(targetClubAlbum.seq)
    }
}