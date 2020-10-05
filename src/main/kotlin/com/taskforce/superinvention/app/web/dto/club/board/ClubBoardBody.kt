package com.taskforce.superinvention.app.web.dto.club.board

import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import java.time.format.DateTimeFormatter

data class ClubBoardBody(
        val title: String,
        val content: String,
        val isTopFixed: Boolean = false,
        val isNotifiable: Boolean = false,
        val imgList: List<S3Path>
)

class ClubBoardSearchOpt(
        val title  : String = "",
        val content: String = ""
)

// 모임 게시판 단일 조회
data class ClubBoardDto(
        val clubBoardSeq: Long,
        val title      : String,
        val content    : String,
        val userName   : String,
        val clubUserSeq: Long,
        val createdAt  : String,
        val updatedAt  : String,
        val topFixedFlag    : Boolean,
        val notificationFlag: Boolean
) {
    constructor(clubBoard: ClubBoard): this(
            clubBoard.seq!!,
            clubBoard.title,
            clubBoard.content,
            clubBoard.clubUser.user.userName!!,
            clubBoard.seq!!,
            clubBoard.createdAt!!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            clubBoard.updatedAt!!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            clubBoard.topFixedFlag,
            clubBoard.notificationFlag
    )
}

// 모임 게시판 리스트 조회
data class ClubBoardPreviewDto(
        val clubBoardSeq: Long,
        val clubUserSeq: Long,
        val title  : String,
        val userName  : String,
        val createdAt  : String,
        val titleImgUrl: String,
        val photoCnt: Long
)