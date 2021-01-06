package com.taskforce.superinvention.app.web.dto.club.album

import com.taskforce.superinvention.app.domain.club.album.ClubAlbum
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.web.dto.user.UserDto

data class ClubAlbumRegisterDto(
        val title     : String = "",
        val file_name : String = "",
        val imgUrl    : String = ""
)

data class ClubAlbumDto(
        val albumSeq   : Long   = 0,
        val title      : String = "",
        val file_name  : String = "",
        val imgUrl     : String = "",
        val likeCnt    : Long = 0,
        val commentCnt : Long = 0,
        val writer     : ClubAlbumWriter
) {
        constructor(clubAlbum: ClubAlbum): this (
                albumSeq   = clubAlbum.seq!!,
                title      = clubAlbum.title,
                file_name  = clubAlbum.file_name,
                imgUrl     = clubAlbum.img_url,
                likeCnt    = clubAlbum.albumLikeCnt    ?: 0,
                commentCnt = clubAlbum.albumCommentCnt ?: 0,
                writer     = ClubAlbumWriter(clubAlbum.writer)
        )
}

data class ClubAlbumListDto(
        val albumSeq   : Long   = 0,
        val title      : String = "",
        val file_name  : String = "",
        val imgUrl     : String = "",
        val likeCnt    : Long,
        val commentCnt : Long,
        val writerClubUserSeq: Long ?= 0
) {
        constructor(clubAlbum: ClubAlbum): this (
                albumSeq   = clubAlbum.seq!!,
                title      = clubAlbum.title,
                file_name  = clubAlbum.file_name,
                imgUrl     = clubAlbum.img_url,
                likeCnt    = clubAlbum.albumLikeCnt ?: 0,
                commentCnt = clubAlbum.albumCommentCnt ?: 0,
                writerClubUserSeq = clubAlbum.writer.seq
        )
}

data class ClubAlbumWriter(
        val writerUserSeq: Long,
        val writerClubUserSeq : Long,
        val name  : String,
        val imgUrl: String,
        val role: List<Role.RoleName>
) {
        constructor(writer: ClubUser): this(
                name               = writer.user.userName ?: "",
                writerClubUserSeq  = writer.seq!!,
                writerUserSeq = writer.user.seq!!,
                imgUrl        = writer.user.profileImageLink ?: "",
                role          = writer.clubUserRoles.map { clubUserRoles -> clubUserRoles.role.name }
        )
}

data class ClubAlbumSearchOption (
        val title: String = ""
)