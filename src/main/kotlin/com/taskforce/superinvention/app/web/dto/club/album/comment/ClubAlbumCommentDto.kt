package com.taskforce.superinvention.app.web.dto.club.album.comment

import com.taskforce.superinvention.app.domain.club.album.comment.ClubAlbumComment
import com.taskforce.superinvention.app.domain.club.album.comment.ClubAlbumCommentCTE
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.common.util.extendFun.toBaseDateTime

data class ClubAlbumCommentRegisterDto(
        val content: String = ""
)

data class ClubAlbumCommentListDto(
        val commentSeq: Long,
        val writer: String,
        val writerSeq: Long,
        val writeClubUserSeq: Long,
        val registerTime: String,
        val content: String,
        val imgUrl: String,
        val isWrittenByMe: Boolean,
        val depth: Long,
        var childCommentCnt   : Long    ? = 0,
        val onlyDirectChildCnt: Boolean ?= false
) {

    constructor(clubAlbumComment: ClubAlbumComment) :this(
            commentSeq       = clubAlbumComment.seq!!,
            content          = clubAlbumComment.content,
            writer           = clubAlbumComment.clubUser.user.userName ?: "",
            registerTime     = clubAlbumComment.createdAt?.toBaseDateTime() ?: "",
            writeClubUserSeq = clubAlbumComment.clubUser.seq!!,
            writerSeq        = clubAlbumComment.clubUser.user.seq!!,
            depth            = clubAlbumComment.depth,
            imgUrl           = clubAlbumComment.clubUser.user.profileImageLink ?: "",
            isWrittenByMe    = false
    ) {
        childCommentCnt = if(onlyDirectChildCnt == true) {
            clubAlbumComment.subCommentCnt
        } else {
            clubAlbumComment.totalSubCommentCnt
        }
    }

    constructor(clubAlbumComment: ClubAlbumComment, user: User): this(
        commentSeq       = clubAlbumComment.seq!!,
        content          = clubAlbumComment.content,
        writer           = clubAlbumComment.clubUser.user.userName ?: "",
        registerTime     = clubAlbumComment.createdAt?.toBaseDateTime() ?: "",
        writeClubUserSeq = clubAlbumComment.clubUser.seq!!,
        writerSeq        = clubAlbumComment.clubUser.user.seq!!,
        depth            = clubAlbumComment.depth,
        imgUrl           = clubAlbumComment.clubUser.user.profileImageLink ?: "",
        isWrittenByMe    = user.seq == clubAlbumComment.clubUser.user.seq
    ) {
        childCommentCnt = if(onlyDirectChildCnt == true) {
            clubAlbumComment.subCommentCnt
        } else {
            clubAlbumComment.totalSubCommentCnt
        }
    }

    constructor(clubAlbumComment: ClubAlbumCommentCTE) :this(
        commentSeq       = clubAlbumComment.seq!!,
        content          = clubAlbumComment.content,
        writer           = clubAlbumComment.clubUser.user.userName ?: "",
        registerTime     = clubAlbumComment.createdAt?.toBaseDateTime() ?: "",
        writeClubUserSeq = clubAlbumComment.clubUser.seq!!,
        writerSeq        = clubAlbumComment.clubUser.user.seq!!,
        imgUrl           = clubAlbumComment.clubUser.user.profileImageLink ?: "",
        depth            = clubAlbumComment.depth,
        childCommentCnt  = clubAlbumComment.subCommentCnt ?: 0,
        isWrittenByMe    = false
    )

    constructor(clubAlbumComment: ClubAlbumCommentCTE, user: User): this(
        commentSeq       = clubAlbumComment.seq!!,
        content          = clubAlbumComment.content,
        writer           = clubAlbumComment.clubUser.user.userName ?: "",
        registerTime     = clubAlbumComment.createdAt?.toBaseDateTime() ?: "",
        writeClubUserSeq = clubAlbumComment.clubUser.seq!!,
        writerSeq        = clubAlbumComment.clubUser.user.seq!!,
        imgUrl           = clubAlbumComment.clubUser.user.profileImageLink ?: "",
        depth            = clubAlbumComment.depth,
        childCommentCnt  = clubAlbumComment.subCommentCnt ?: 0,
        isWrittenByMe    = user.seq == clubAlbumComment.clubUser.user.seq
    )
}