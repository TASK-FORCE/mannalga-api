package com.taskforce.superinvention.app.web.dto.meeting

import com.taskforce.superinvention.app.domain.meeting.MeetingApplication
import com.taskforce.superinvention.app.web.dto.club.ClubUserDto
import com.taskforce.superinvention.common.util.extendFun.toBaseDateTime
class MeetingApplicationDto(
        val seq: Long,
        val clubUser: ClubUserDto,
        val deleteFlag: Boolean,
        val createdAt: String,
        val updatedAt: String?
) {
    constructor(meetingApplication: MeetingApplication): this(
            seq = meetingApplication.seq!!,
            clubUser = ClubUserDto(meetingApplication.clubUser),
            deleteFlag = meetingApplication.deleteFlag,
            createdAt = meetingApplication.createdAt!!.toBaseDateTime(),
            updatedAt = meetingApplication.updatedAt?.toBaseDateTime() ?:""
    )
}