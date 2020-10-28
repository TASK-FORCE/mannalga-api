package com.taskforce.superinvention.app.web.dto.meeting

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.taskforce.superinvention.app.domain.meeting.Meeting
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
    constructor(meeting: Meeting): this(
            seq = meeting.seq!!,
            title = meeting.title,
            content = meeting.content,
            startTimestamp = meeting.startTimestamp.toBaseDateTime(),
            endTimestamp = meeting.endTimestamp.toBaseDateTime(),
            club = ClubDto(meeting.club, null),
            deleteFlag = meeting.deleteFlag,
            maximumNumber = meeting.maximumNumber,
            regClubUser = ClubUserDto(meeting.regClubUser)
    )
}


class MeetingAddRequestDto(
        val title: String,
        val content: String,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT, timezone = "Asia/Seoul")
        val startTimestamp: LocalDateTime,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT, timezone = "Asia/Seoul")
        val endTimestamp: LocalDateTime,
        val maximumNumber: Int?
) {

}



