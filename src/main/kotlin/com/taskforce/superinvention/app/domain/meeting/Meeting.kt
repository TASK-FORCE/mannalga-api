package com.taskforce.superinvention.app.domain.meeting

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import java.time.LocalDateTime
import javax.persistence.*

@Entity
class Meeting(
        var title: String,
        var content: String,
        var startTimestamp: LocalDateTime,
        var endTimestamp: LocalDateTime,
        @ManyToOne
        var club: Club,
        var deleteFlag: Boolean,
        var maximumNumber: Int?,
        @ManyToOne
        var regClubUser: ClubUser
) : BaseEntity() {
        @OneToMany(mappedBy = "meeting")
        var meetingApplications: List<MeetingApplication> = listOf()
}