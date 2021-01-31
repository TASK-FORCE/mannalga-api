package com.taskforce.superinvention.app.web.dto.club.board

import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.app.domain.club.board.img.ClubBoardImg
import com.taskforce.superinvention.app.web.dto.club.ClubWriter
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import com.taskforce.superinvention.common.util.extendFun.sliceIfExceed
import com.taskforce.superinvention.common.util.extendFun.toBaseDateTime
import javax.validation.constraints.NotBlank

data class ClubBoardRegisterBody(
        @get:NotBlank(message = "게시판 제목을 입력해주세요")
        val title   : String,

        @get:NotBlank(message = "게시판 내용은 공백일 수 없습니다")
        val content : String,

        val category: ClubBoard.Category,
        val imgList : List<S3Path> = emptyList()
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
        val createdAt  : String,
        val updatedAt  : String,
        val boardImgs  : List<ClubBoardImg>,
        val writer     : ClubWriter
) {
    constructor(clubBoard: ClubBoard): this(
            clubBoardSeq = clubBoard.seq!!,
            title     = clubBoard.title,
            content   = clubBoard.content,
            createdAt = clubBoard.createdAt?.toBaseDateTime() ?: "",
            updatedAt = clubBoard.updatedAt?.toBaseDateTime() ?: "",
            boardImgs = clubBoard.boardImgs,
            writer    = ClubWriter(clubBoard.clubUser)
    )
}

// 모임 게시판 리스트 조회
data class ClubBoardListViewDto(
        val boardSeq     : Long,
        val title        : String,
        val simpleContent: String,
        val mainImageUrl : String,
        val createAt     : String,
        val category     : String,
        val likeCnt      : Long,
        val commentCnt   : Long,
        val writer       : ClubWriter

) {
        constructor(clubBoard: ClubBoard): this(
                boardSeq = clubBoard.seq!!,
                title    = clubBoard.title,
                simpleContent = clubBoard.content.sliceIfExceed(0 until 50),
                mainImageUrl  = clubBoard.boardImgs.firstOrNull()?.imgUrl ?: "",
                createAt      = clubBoard.createdAt?.toBaseDateTime()     ?: "",
                category   = clubBoard.category.label,
                likeCnt    = clubBoard.boardLikeCnt    ?: 0,
                commentCnt = clubBoard.boardCommentCnt ?: 0,
                writer     = ClubWriter(clubBoard.clubUser)
        )
}