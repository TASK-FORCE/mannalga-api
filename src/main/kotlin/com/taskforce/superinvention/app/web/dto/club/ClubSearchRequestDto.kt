package com.taskforce.superinvention.app.web.dto.club

import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.state.StateRequestDto

class ClubSearchRequestDto(
        val offset:Long = 0,
        val size:Long = 10,
        val searchOptions: ClubSearchOptions
)

class ClubSearchOptions(
    var stateList: List<StateRequestDto>,
    var interestList: List<InterestRequestDto>
)