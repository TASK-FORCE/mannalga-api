package com.taskforce.superinvention.app.web.dto.club.album.comment

import com.taskforce.superinvention.app.domain.club.album.comment.ClubAlbumComment
import com.taskforce.superinvention.common.util.extendFun.toBaseDateTime

data class ClubAlbumCommentRegisterDto(
        val content: String = ""
)

data class ClubAlbumCommentListDto(
        val writer: String,
        val writeClubUserSeq: Long,
        val registerTime: String,
        val content: String
) {
    constructor(clubAlbumComment: ClubAlbumComment) :this(
            content          = clubAlbumComment.content,
            writer           = clubAlbumComment.clubUser.user.userName ?: "",
            writeClubUserSeq = clubAlbumComment.clubUser.seq ?: 0,
            registerTime     = clubAlbumComment.createdAt?.toBaseDateTime() ?: ""
    )
}