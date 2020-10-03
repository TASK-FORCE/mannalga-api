package com.taskforce.superinvention.app.web.dto.club

import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto

class ClubAddRequestDto(
        var name: String,
        var description: String,
        var maximumNumber: Long,
        var mainImageUrl: String?,
        var interestList: List<InterestRequestDto>,
        var regionList: List<RegionRequestDto>
) {
}