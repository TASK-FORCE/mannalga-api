package com.taskforce.superinvention.app.web.dto.meeting

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.taskforce.superinvention.app.domain.meeting.Meeting
import com.taskforce.superinvention.app.domain.meeting.MeetingApplication
import com.taskforce.superinvention.app.web.dto.club.ClubDto
import com.taskforce.superinvention.app.web.dto.club.ClubUserDto
import com.taskforce.superinvention.common.util.extendFun.DATE_TIME_FORMAT
import com.taskforce.superinvention.common.util.extendFun.toBaseDateTime
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime

class MeetingDto(
        val seq: Long,
        val title: String,
        val content: String,
        val startTimestamp: String,
        val endTimestamp: String,
        val club: ClubDto,
        val deleteFlag: Boolean,
        val maximumNumber: Int?,
        val regClubUser: ClubUserDto
) {
    lateinit var meetingApplications: List<MeetingApplicationDto>


    constructor(meeting: Meeting): this(
            seq = meeting.seq!!,
            title = meeting.title,
            content = meeting.content,
            startTimestamp = meeting.startTimestamp.toBaseDateTime(),
            endTimestamp = meeting.endTimestamp.toBaseDateTime(),
            club = ClubDto(meeting.club),
            deleteFlag = meeting.deleteFlag,
            maximumNumber = meeting.maximumNumber,
            regClubUser = ClubUserDto(meeting.regClubUser)
    ) {
        meetingApplications = meeting.meetingApplications.map { e -> this.MeetingApplicationDto(e) }
    }


    inner class MeetingApplicationDto {
        val seq: Long
        val deleteFlag: Boolean
        val createdAt: String
        val updatedAt: String?
        val userInfo: UserInfo

        constructor(meetingApplication: MeetingApplication) {
            val user = meetingApplication.clubUser.user
            userInfo = UserInfo(
                    userSeq = user.seq!!,
                    userName = user.userName!!,
                    profileImageLink = user.profileImageLink
            )
            seq = meetingApplication.seq!!
            deleteFlag = meetingApplication.deleteFlag
            createdAt = meetingApplication.createdAt!!.toBaseDateTime()
            updatedAt = meetingApplication.updatedAt?.toBaseDateTime() ?: ""
        }

        inner class UserInfo (
                val userSeq: Long,
                val userName: String,
                val profileImageLink: String?
        )
    }
}


class MeetingRequestDto(
        val title: String,
        val content: String,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT, timezone = "Asia/Seoul")
        val startTimestamp: LocalDateTime,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT, timezone = "Asia/Seoul")
        val endTimestamp: LocalDateTime,
        val maximumNumber: Int?
) {

}



