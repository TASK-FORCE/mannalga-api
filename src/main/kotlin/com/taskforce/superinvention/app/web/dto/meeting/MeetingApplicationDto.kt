package com.taskforce.superinvention.app.web.dto.meeting

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.meeting.Meeting
import com.taskforce.superinvention.app.domain.meeting.MeetingApplication
import com.taskforce.superinvention.app.web.dto.club.ClubUserDto
import com.taskforce.superinvention.common.util.extendFun.toBaseDateTime
import java.time.LocalDateTime

class MeetingApplicationDto(
        val seq: Long,
        val clubUser: ClubUserDto,
        val meeting: MeetingDto,
        val deleteFlag: Boolean,
        val createdAt: String,
        val updatedAt: String?
) {
    constructor(meetingApplication: MeetingApplication): this(
            seq = meetingApplication.seq!!,
            clubUser = ClubUserDto(meetingApplication.clubUser),
            meeting = MeetingDto(meetingApplication.meeting),
            deleteFlag = meetingApplication.deleteFlag,
            createdAt = meetingApplication.createdAt!!.toBaseDateTime(),
            updatedAt = meetingApplication.updatedAt?.toBaseDateTime() ?:""
    ) {

    }
}
