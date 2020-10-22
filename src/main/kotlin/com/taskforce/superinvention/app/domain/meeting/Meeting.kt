package com.taskforce.superinvention.app.domain.meeting

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.Club
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
        var maximumNumber: Int?
) : BaseEntity() {
        @OneToMany(fetch = FetchType.LAZY)
        @JoinColumn(name = "meeting_seq")
        lateinit var meetingApplications: List<MeetingApplication>
}