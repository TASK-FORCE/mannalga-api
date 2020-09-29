package com.taskforce.superinvention.app.web.dto.club

import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto

class ClubSearchRequestDto(
        val page:Long = 0,
        val size:Long = 10,
        val searchOptions: ClubSearchOptions
)

class ClubSearchOptions(
        var regionList: List<RegionRequestDto>,
        var interestList: List<InterestRequestDto>
)