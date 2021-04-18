package com.taskforce.superinvention.app.domain.club.album.like

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.album.ClubAlbum
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import javax.persistence.*

@Entity
class ClubAlbumLike(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_album_seq")
    val clubAlbum: ClubAlbum,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "club_user_seq")
    val clubUser: ClubUser
): BaseEntity()