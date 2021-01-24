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
import com.taskforce.superinvention.common.util.extendFun.DATE_TIME_FORMAT
import com.taskforce.superinvention.common.util.extendFun.toBaseDateTime
import java.time.LocalDateTime
import javax.validation.constraints.Max
import javax.validation.constraints.Min
import javax.validation.constraints.NotBlank

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
    var region: String?
    var regionURL: String?
    var cost: Int?
    var isOpen: Boolean


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
        region = meeting.region
        regionURL = meeting.regionURL
        cost = meeting.cost
        isOpen = meeting.isOpen()

        meetingApplications = meeting.meetingApplications.map(::MeetingApplicationDto)
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
            isCurrentUserApplicationMeeting: Boolean,
            region: String?,
            regionURL: String?,
            cost: Int?,
            isOpen: Boolean
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
        this.region = region
        this.regionURL = regionURL
        this.cost = cost
        this.isOpen = isOpen
    }



    class MeetingApplicationDto {
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


data class MeetingRequestDto(
        @get:NotBlank(message = "만남 제목을 입력해주세요")
        val title: String,
        @get:NotBlank(message = "만남 내용을 입력해주세요")
        val content: String,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT, timezone = "Asia/Seoul")
        val startTimestamp: LocalDateTime,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DATE_TIME_FORMAT, timezone = "Asia/Seoul")
        val endTimestamp: LocalDateTime,
        @get:Max(value = 999, message = "최대 인원은 999명까지만 제한할 수 있습니다.")
        @get:Min(2, message = "만남 최소 인원은 2명 이상이어야 합니다.")
        val maximumNumber: Int?,
        val region: String?,
        var regionURL: String?,
        val cost: Int?
)


