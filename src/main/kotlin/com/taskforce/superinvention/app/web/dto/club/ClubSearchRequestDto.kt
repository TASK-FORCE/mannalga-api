package com.taskforce.superinvention.app.web.dto.club

import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto


class ClubSearchRequestDto {
    var regionSeq: Long? = null
    var interestSeq: Long? = null
    var interestGroupSeq: Long? = null

    constructor(regionSeq: Long?, interestSeq: Long?, interestGroupSeq: Long?) {
        this.regionSeq = regionSeq
        this.interestSeq = interestSeq
        this.interestGroupSeq = interestGroupSeq
    }
}