package com.taskforce.superinvention.app.web.dto.club.album.comment

import com.taskforce.superinvention.app.domain.club.album.comment.ClubAlbumComment
import com.taskforce.superinvention.common.exception.BizException
import com.taskforce.superinvention.common.util.extendFun.toBaseDateTime
import org.springframework.http.HttpStatus

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
            registerTime     = clubAlbumComment.createdAt?.toBaseDateTime() ?: "",
            writeClubUserSeq = clubAlbumComment.clubUser.seq
                    ?: throw BizException("작성자를 확인할 수 없습니다.", HttpStatus.NOT_FOUND)
    )
}