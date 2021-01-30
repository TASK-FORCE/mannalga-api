package com.taskforce.superinvention.app.web.dto.club.board

import com.taskforce.superinvention.app.domain.club.board.ClubBoard
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
        val userName   : String,
        val clubUserSeq: Long,
        val createdAt  : String,
        val updatedAt  : String,
) {
    constructor(clubBoard: ClubBoard): this(
            clubBoard.seq!!,
            clubBoard.title,
            clubBoard.content,
            clubBoard.clubUser.user.userName!!,
            clubBoard.seq!!,
            clubBoard.createdAt?.toBaseDateTime() ?: "",
            clubBoard.updatedAt?.toBaseDateTime() ?: "",
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
                mainImageUrl  = clubBoard.firstImg ?: "",
                createAt      = clubBoard.createdAt?.toBaseDateTime() ?: "",
                category   = clubBoard.category.label,
                likeCnt    = 0,  // @todo
                commentCnt = clubBoard.boardCommentCnt ?: 0,
                writer     = ClubWriter(clubBoard.clubUser)
        )
}