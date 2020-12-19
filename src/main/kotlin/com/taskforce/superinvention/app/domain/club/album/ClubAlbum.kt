package com.taskforce.superinvention.app.domain.club.album

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.album.comment.ClubAlbumComment
import com.taskforce.superinvention.app.domain.club.album.like.ClubAlbumLike
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumRegisterDto
import javax.persistence.*

@Entity
class ClubAlbum(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_seq")
    val club       : Club,
    var title      : String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_user_seq")
    var writer   : ClubUser,

    var img_url    : String,
    var file_name  : String,
    var delete_flag: Boolean
): BaseEntity() {

    @OneToMany
    @JoinColumn(name = "club_album_seq")
    lateinit var clubAlbumLikes: List<ClubAlbumLike>

    @OneToMany
    @JoinColumn(name = "club_album_seq")
    lateinit var clubAlbumComments: List<ClubAlbumComment>

    constructor(writer: ClubUser, club: Club, registerDto: ClubAlbumRegisterDto): this (
            club        = club,
            writer    = writer,
            title       = registerDto.title,
            img_url     = registerDto.imgUrl,
            file_name   = registerDto.file_name,
            delete_flag = false
    )
}