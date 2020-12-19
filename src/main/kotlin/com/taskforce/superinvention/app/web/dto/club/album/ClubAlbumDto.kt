package com.taskforce.superinvention.app.web.dto.club.album

data class ClubAlbumRegisterDto(
        val title     : String = "",
        val file_name : String = "",
        val imgUrl    : String = ""
)

data class ClubAlbumListDto(
        val title      : String = "",
        val file_name  : String = "",
        val imgUrl     : String = "",
        val likeCnt    : Long = 0,
        val commentCnt : Long = 0
)

data class ClubAlbumSearchOption (
        val title: String = ""
)