package com.taskforce.superinvention.app.domain.interest.interest

import com.taskforce.superinvention.app.domain.interest.ClubInterest

class InterestDto {
    var seq: Long?
    var name: String

    constructor(seq: Long?, name: String) {
        this.seq  = seq
        this.name = name
    }

    constructor(interest: Interest) {
        this.seq = interest.seq
        this.name = interest.name
    }
}
