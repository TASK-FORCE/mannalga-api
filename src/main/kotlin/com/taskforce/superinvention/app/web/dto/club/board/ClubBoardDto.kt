package com.taskforce.superinvention.app.web.dto.club.board

import com.taskforce.superinvention.app.web.dto.club.ClubSearchOptions

class ClubBoardDto(
        val title: String,
        val content: String
)

class ClubBoardSearchRequestDto(
        val offset:Long = 0,
        val size  :Long = 10,
        val searchOptions: ClubBoardSearchOption
)

class ClubBoardSearchOption {

}