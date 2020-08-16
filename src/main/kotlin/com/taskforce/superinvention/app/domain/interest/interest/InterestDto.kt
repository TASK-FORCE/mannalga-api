package com.taskforce.superinvention.app.domain.interest.interest

class InterestDto {
    var seq: Long?
    var name: String

    constructor(seq: Long?, name: String) {
        this.seq  = seq
        this.name = name
    }
}
