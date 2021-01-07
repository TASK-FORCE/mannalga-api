package com.taskforce.superinvention.app.web.dto.meeting

import com.taskforce.superinvention.app.domain.meeting.Meeting
import com.taskforce.superinvention.app.domain.meeting.MeetingApplication
import com.taskforce.superinvention.app.web.dto.club.ClubUserDto
import com.taskforce.superinvention.app.web.dto.club.ClubUserWithUserDto
import com.taskforce.superinvention.common.util.extendFun.toBaseDateTime

class MeetingApplicationStatusDto(
        val meetingApplications: List<MeetingDto.MeetingApplicationDto>,
        val currentCount: Int,
        val maximumNumber: Int?,
        var isCurrentUserRegMeeting: Boolean = false,
        var isCurrentUserApplicationMeeting: Boolean = false
) {
    constructor(meeting: Meeting, currentClubUserSeq: Long?): this(
            meetingApplications = meeting.meetingApplications.map { e -> MeetingDto.MeetingApplicationDto(e) },
            currentCount = meeting.meetingApplications.filterNot { it.deleteFlag }.count(),
            maximumNumber = meeting.maximumNumber,
            isCurrentUserRegMeeting = currentClubUserSeq == meeting.regClubUser.seq,
            isCurrentUserApplicationMeeting = meeting.meetingApplications.filter { !it.deleteFlag }.map { e -> e.clubUser.seq }.contains(currentClubUserSeq)
    )
}

class MeetingApplicationDto(
        val seq: Long,
        val clubUser: ClubUserWithUserDto,
        val deleteFlag: Boolean,
        val createdAt: String,
        val updatedAt: String?
) {
    constructor(meetingApplication: MeetingApplication): this(
            seq = meetingApplication.seq!!,
            clubUser = ClubUserWithUserDto(meetingApplication.clubUser),
            deleteFlag = meetingApplication.deleteFlag,
            createdAt = meetingApplication.createdAt!!.toBaseDateTime(),
            updatedAt = meetingApplication.updatedAt?.toBaseDateTime() ?:""
    )
}