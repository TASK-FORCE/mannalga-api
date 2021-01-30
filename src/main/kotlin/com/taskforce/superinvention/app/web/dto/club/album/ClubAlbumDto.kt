package com.taskforce.superinvention.app.web.dto.club.album

import com.taskforce.superinvention.app.domain.club.album.ClubAlbum
import com.taskforce.superinvention.app.web.dto.club.ClubWriter

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
        val writer     : ClubWriter,
        val isLiked    : Boolean
) {
        constructor(clubAlbum: ClubAlbum, isLiked: Boolean): this (
                albumSeq   = clubAlbum.seq!!,
                title      = clubAlbum.title,
                file_name  = clubAlbum.file_name,
                imgUrl     = clubAlbum.img_url,
                likeCnt    = clubAlbum.albumLikeCnt    ?: 0,
                commentCnt = clubAlbum.albumCommentCnt ?: 0,
                writer     = ClubWriter(clubAlbum.writer),
                isLiked    = isLiked
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

data class ClubAlbumSearchOption (
        val title: String = ""
)