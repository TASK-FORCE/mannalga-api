package com.taskforce.superinvention.app.web.dto.club.board.comment

import com.taskforce.superinvention.app.domain.club.board.comment.ClubBoardComment
import com.taskforce.superinvention.app.domain.club.board.comment.ClubBoardCommentCTE
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.common.util.extendFun.toBaseDateTime

data class ClubBoardCommentRegisterDto(
    val content: String = ""
)

data class ClubBoardCommentListDto(
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

    constructor(clubBoardComment: ClubBoardComment) :this(
        commentSeq       = clubBoardComment.seq!!,
        content          = clubBoardComment.content,
        writer           = clubBoardComment.clubUser.user.userName ?: "",
        registerTime     = clubBoardComment.createdAt?.toBaseDateTime() ?: "",
        writeClubUserSeq = clubBoardComment.clubUser.seq!!,
        writerSeq        = clubBoardComment.clubUser.user.seq!!,
        depth            = clubBoardComment.depth,
        imgUrl           = clubBoardComment.clubUser.user.profileImageLink ?: "",
        isWrittenByMe    = false
    ) {
        childCommentCnt = if(onlyDirectChildCnt == true) {
            clubBoardComment.subCommentCnt
        } else {
            clubBoardComment.totalSubCommentCnt
        }
    }

    constructor(clubBoardComment: ClubBoardComment, user: User): this(
        commentSeq       = clubBoardComment.seq!!,
        content          = clubBoardComment.content,
        writer           = clubBoardComment.clubUser.user.userName ?: "",
        registerTime     = clubBoardComment.createdAt?.toBaseDateTime() ?: "",
        writeClubUserSeq = clubBoardComment.clubUser.seq!!,
        writerSeq        = clubBoardComment.clubUser.user.seq!!,
        depth            = clubBoardComment.depth,
        imgUrl           = clubBoardComment.clubUser.user.profileImageLink ?: "",
        isWrittenByMe    = user.seq == clubBoardComment.clubUser.user.seq
    ) {
        childCommentCnt = if(onlyDirectChildCnt == true) {
            clubBoardComment.subCommentCnt
        } else {
            clubBoardComment.totalSubCommentCnt
        }
    }

    constructor(clubBoardComment: ClubBoardCommentCTE) :this(
        commentSeq       = clubBoardComment.seq!!,
        content          = clubBoardComment.content,
        writer           = clubBoardComment.clubUser.user.userName ?: "",
        registerTime     = clubBoardComment.createdAt?.toBaseDateTime() ?: "",
        writeClubUserSeq = clubBoardComment.clubUser.seq!!,
        writerSeq        = clubBoardComment.clubUser.user.seq!!,
        imgUrl           = clubBoardComment.clubUser.user.profileImageLink ?: "",
        depth            = clubBoardComment.depth,
        childCommentCnt  = clubBoardComment.subCommentCnt ?: 0,
        isWrittenByMe    = false
    )

    constructor(clubBoardComment: ClubBoardCommentCTE, user: User): this(
        commentSeq       = clubBoardComment.seq!!,
        content          = clubBoardComment.content,
        writer           = clubBoardComment.clubUser.user.userName ?: "",
        registerTime     = clubBoardComment.createdAt?.toBaseDateTime() ?: "",
        writeClubUserSeq = clubBoardComment.clubUser.seq!!,
        writerSeq        = clubBoardComment.clubUser.user.seq!!,
        imgUrl           = clubBoardComment.clubUser.user.profileImageLink ?: "",
        depth            = clubBoardComment.depth,
        childCommentCnt  = clubBoardComment.subCommentCnt ?: 0,
        isWrittenByMe    = user.seq == clubBoardComment.clubUser.user.seq
    )
}