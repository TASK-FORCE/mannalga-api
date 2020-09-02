package com.taskforce.superinvention.app.web.dto.club

import com.taskforce.superinvention.app.domain.club.Club

class ClubDto (
        var seq: Long?,
        var name: String,
        var description: String,
        var maximumNumber: Long,
        var userCount: Long
){
    constructor(club : Club, userCount: Long): this(club.seq, club.name, club.description, club.maximumNumber, userCount)
}