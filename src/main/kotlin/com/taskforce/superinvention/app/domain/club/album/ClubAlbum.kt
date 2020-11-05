package com.taskforce.superinvention.app.domain.club.album

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumRegisterDto
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class ClubAlbum(

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_seq")
    val club       : Club,
    var title      : String,
    var img_url    : String,
    var file_name  : String,
    var delete_flag: Boolean
): BaseEntity() {

    constructor(club: Club, dto: ClubAlbumRegisterDto): this (
            club    = club,
            title   = dto.title,
            img_url = dto.img_ur,
            file_name   = dto.file_name,
            delete_flag = false
    )
}