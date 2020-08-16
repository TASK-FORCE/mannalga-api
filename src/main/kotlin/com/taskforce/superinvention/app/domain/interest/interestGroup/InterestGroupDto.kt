package com.taskforce.superinvention.app.domain.interest.interestGroup

import com.taskforce.superinvention.app.domain.interest.interest.Interest
import com.taskforce.superinvention.app.domain.interest.interest.InterestDto

class InterestGroupDto {
    var name: String
    var groupSeq: Long?
    var interestList: List<InterestDto>

    constructor(group: InterestGroup, interestList: List<Interest> ) {
        this.name = group.name
        this.groupSeq = group.seq
        this.interestList = interestList.map { interest -> InterestDto(interest.seq, interest.name)}.toMutableList()
    }

    constructor(groupSeq: Long, name: String, interestList: List<InterestDto> ) {
        this.name = name
        this.groupSeq = groupSeq
        this.interestList = interestList
    }
}

