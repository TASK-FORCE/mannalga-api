package com.taskforce.superinvention.app.web.dto.meeting

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.meeting.Meeting
import com.taskforce.superinvention.app.domain.meeting.MeetingApplication
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.ClubDto
import com.taskforce.superinvention.app.web.dto.club.ClubUserDto
import com.taskforce.superinvention.app.web.dto.club.ClubUserWithUserDto
import com.taskforce.superinvention.app.web.dto.role.RoleDto
import com.taskforce.superinvention.app.web.dto.user.info.UserInfoDto
import com.taskforce.superinvention.common.util.extendFun.DATE_TIME_FORMAT
import com.taskforce.superinvention.common.util.extendFun.toBaseDateTime
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.security.core.context.SecurityContextHolder
import java.time.LocalDateTime
import kotlin.math.max

class MeetingDto {
    val seq: Long
    val title: String
    val content: String
    val startTimestamp: String
    val endTimestamp: String
    val club: ClubDto
    val deleteFlag: Boolean
    val maximumNumber: Int?
    val regClubUser: ClubUserWithUserDto
    var meetingApplications: List<MeetingApplicationDto>
    var isCurrentUserRegMeeting: Boolean
    var isCurrentUserApplicationMeeting: Boolean


    constructor(meeting: Meeting, currentClubUserSeq: Long?){
        seq = meeting.seq!!
        title = meeting.title
        content = meeting.content
        startTimestamp = meeting.startTimestamp.toBaseDateTime()
        endTimestamp = meeting.endTimestamp.toBaseDateTime()
        club = ClubDto(meeting.club)
        deleteFlag = meeting.deleteFlag
        maximumNumber = meeting.maximumNumber
        regClubUser = ClubUserWithUserDto(meeting.regClubUser)

        meetingApplications = meeting.meetingApplications.map { e -> MeetingApplicationDto(e) }
        isCurrentUserRegMeeting = currentClubUserSeq == regClubUser.seq
        isCurrentUserApplicationMeeting = meeting.meetingApplications.filter { !it.deleteFlag }.map { e -> e.clubUser.seq }.contains(currentClubUserSeq)
    }

    constructor(
            seq: Long,
            title: String,
            content: String,
            startTimestamp: String,
            endTimestamp: String,
            club: ClubDto,
            deleteFlag: Boolean,
            maximumNumber: Int,
            regClubUser: ClubUserWithUserDto,
            meetingApplications: List<MeetingApplicationDto>,
            currentClubUserSeq: Long?,
            isCurrentUserApplicationMeeting: Boolean
    ) {
        this.seq = seq
        this.title = title
        this.content = content
        this.startTimestamp = startTimestamp
        this.endTimestamp = endTimestamp
        this.club = club
        this.deleteFlag = deleteFlag
        this.maximumNumber = maximumNumber
        this.regClubUser = regClubUser
        this.meetingApplications = meetingApplications
        this.isCurrentUserRegMeeting = currentClubUserSeq == regClubUser.seq
        this.isCurrentUserApplicationMeeting = isCurrentUserApplicationMeeting
    }



    inner class MeetingApplicationDto {
        val seq: Long
        val deleteFlag: Boolean
        val createdAt: String
        val updatedAt: String?
        val userInfo: UserInfo

        constructor(meetingApplication: MeetingApplication) {
            val clubUser = meetingApplication.clubUser
            val user = clubUser.user

            userInfo = UserInfo(
                    userSeq = user.seq!!,
                    userName = user.userName!!,
                    profileImageLink = user.profileImageLink,
                    regUserFlag = meetingApplication.meeting.regClubUser.seq == clubUser.seq
            )

            seq = meetingApplication.seq!!
            deleteFlag = meetingApplication.deleteFlag
            createdAt = meetingApplication.createdAt!!.toBaseDateTime()
            updatedAt = meetingApplication.updatedAt?.toBaseDateTime() ?: ""
        }

        constructor(
                seq: Long,
                deleteFlag: Boolean,
                createdAt: String,
                updatedAt: String?,
                userSeq: Long,
                userName: String,
                profileImageLink: String?,
                regUserFlag: Boolean
        ) {
            this.seq = seq
            this.deleteFlag = deleteFlag
            this.createdAt = createdAt
            this.updatedAt = updatedAt
            this.userInfo = UserInfo(
                    userSeq = userSeq,
                    userName = userName,
                    profileImageLink = profileImageLink,
                    regUserFlag = regUserFlag
            )
        }

        inner class UserInfo (
                val userSeq: Long,
                val userName: String,
                val profileImageLink: String?,
                val regUserFlag: Boolean
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
)


