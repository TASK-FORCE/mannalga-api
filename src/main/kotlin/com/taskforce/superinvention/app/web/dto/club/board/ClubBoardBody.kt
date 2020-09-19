package com.taskforce.superinvention.app.web.dto.club.board

import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import java.time.format.DateTimeFormatter

class ClubBoardBody(
        val title: String,
        val content: String
)

class ClubBoardSearchOpt(
        val title  : String ?= "",
        val content: String ?= ""
)

class ClubBoardDto(
        val title  : String,
        val content: String,
        val userName  : String,
        val createdAt : String,
        val updatedAt : String,
        val topFixedFlag    : Boolean,
        val notificationFlag: Boolean
) {
    constructor(clubBoard: ClubBoard): this(
            clubBoard.title,
            clubBoard.content,
            clubBoard.clubUser.user.userName!!,
            clubBoard.createdAt!!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            clubBoard.updatedAt!!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
            clubBoard.topFixedFlag,
            clubBoard.notificationFlag
    )
}