package com.taskforce.superinvention.app.web.dto.club

import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto


class ClubSearchRequestDto {
    var regionSeq: Long? = null
    var interestSeq: Long? = null

    constructor(regionSeq: Long?, interestSeq: Long?) {
        this.regionSeq = regionSeq
        this.interestSeq = interestSeq
    }
}