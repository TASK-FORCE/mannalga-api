package com.taskforce.superinvention.app.domain.interest.interest

import com.taskforce.superinvention.app.domain.interest.interestGroup.SimpleInterestGroupDto

class InterestDto {
    var seq: Long?
    var name: String
    var interestGroup: SimpleInterestGroupDto

    constructor(seq: Long?, name: String, interestGroup: SimpleInterestGroupDto) {
        this.seq  = seq
        this.name = name
        this.interestGroup = interestGroup
    }

    constructor(interest: Interest) {
        this.seq = interest.seq
        this.name = interest.name
        this.interestGroup = SimpleInterestGroupDto(interest.interestGroup)
    }
}
