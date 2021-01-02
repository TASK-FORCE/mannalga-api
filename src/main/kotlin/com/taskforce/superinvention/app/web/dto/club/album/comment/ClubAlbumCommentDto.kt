package com.taskforce.superinvention.app.web.dto.club.album.comment

import com.taskforce.superinvention.app.domain.club.album.comment.ClubAlbumComment
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.common.exception.BizException
import com.taskforce.superinvention.common.util.extendFun.toBaseDateTime
import org.springframework.http.HttpStatus

data class ClubAlbumCommentRegisterDto(
        val content: String = ""
)

data class ClubAlbumCommentListDto(
        val writer: String,
        val writerSeq: Long,
        val writeClubUserSeq: Long,
        val registerTime: String,
        val content: String,
        val imgUrl: String,
        val isWrittenByMe: Boolean
) {
    constructor(clubAlbumComment: ClubAlbumComment) :this(
            content          = clubAlbumComment.content,
            writer           = clubAlbumComment.clubUser.user.userName ?: "",
            registerTime     = clubAlbumComment.createdAt?.toBaseDateTime() ?: "",
            writeClubUserSeq = clubAlbumComment.clubUser.seq!!,
            writerSeq        = clubAlbumComment.clubUser.user.seq!!,
            imgUrl           = clubAlbumComment.clubUser.user.profileImageLink ?: "",
            isWrittenByMe    = false
    )

    constructor(clubAlbumComment: ClubAlbumComment, user: User): this(
        content          = clubAlbumComment.content,
        writer           = clubAlbumComment.clubUser.user.userName ?: "",
        registerTime     = clubAlbumComment.createdAt?.toBaseDateTime() ?: "",
        writeClubUserSeq = clubAlbumComment.clubUser.seq!!,
        writerSeq        = clubAlbumComment.clubUser.user.seq!!,
        imgUrl           = user.profileImageLink ?: "",
        isWrittenByMe    = user.seq == clubAlbumComment.clubUser.user.seq
    )
}