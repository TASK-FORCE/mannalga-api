package com.taskforce.superinvention.app.web.dto.club

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.interest.ClubInterest
import com.taskforce.superinvention.app.domain.interest.interest.InterestDto
import com.taskforce.superinvention.app.domain.state.ClubState
import com.taskforce.superinvention.app.web.dto.state.SimpleStateDto
import com.taskforce.superinvention.app.web.dto.state.StateDto

class ClubDto (
        var seq: Long?,
        var name: String,
        var description: String,
        var maximumNumber: Long,
        var userCount: Long,
        var mainImageUrl: String?
){
    constructor(club : Club, userCount: Long): this(club.seq, club.name, club.description, club.maximumNumber, userCount, club.mainImageUrl)
}

class ClubWithStateInterestDto (
        var seq: Long?,
        var name: String,
        var description: String,
        var maximumNumber: Long,
        var userCount: Long,
        var mainImageUrl: String?,
        var interests: List<InterestDto>,
        var states: List<SimpleStateDto>
) {
    constructor(club : Club,
                userCount: Long,
                interests: List<InterestDto>,
                states: List<SimpleStateDto>):
            this(club.seq,
                    club.name,
                    club.description,
                    club.maximumNumber,
                    userCount,
                    club.mainImageUrl,
                    interests,
                    states)
}