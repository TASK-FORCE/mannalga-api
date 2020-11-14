package com.taskforce.superinvention.app.web.dto.meeting

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.meeting.Meeting
import com.taskforce.superinvention.app.domain.meeting.MeetingApplication
import com.taskforce.superinvention.app.web.dto.club.ClubUserDto
import java.time.LocalDateTime

@JsonIdentityInfo(property = "objId", generator = ObjectIdGenerators.StringIdGenerator::class)
class MeetingApplicationDto(
        val clubUser: ClubUserDto,
        val meeting: MeetingDto,
        val deleteFlag: Boolean,
        val createdAt: LocalDateTime?,
        val updatedAt: LocalDateTime?
) {
    constructor(meetingApplication: MeetingApplication): this(
            clubUser = ClubUserDto(meetingApplication.clubUser),
            meeting = MeetingDto(meetingApplication.meeting),
            deleteFlag = meetingApplication.deleteFlag,
            createdAt = meetingApplication.createdAt,
            updatedAt = meetingApplication.updatedAt
    ) {

    }
}
