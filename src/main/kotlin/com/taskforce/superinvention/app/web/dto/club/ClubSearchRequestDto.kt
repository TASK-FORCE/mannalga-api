package com.taskforce.superinvention.app.web.dto.club

class ClubSearchRequestDto {
    var regionSeq: Long? = null
    var interestSeq: Long? = null
    var interestGroupSeq: Long? = null
    var text: String? = null

    constructor(regionSeq: Long?, interestSeq: Long?, interestGroupSeq: Long?, query: String?) {
        this.regionSeq = regionSeq
        this.interestSeq = interestSeq
        this.interestGroupSeq = interestGroupSeq
        this.text = query
    }
}