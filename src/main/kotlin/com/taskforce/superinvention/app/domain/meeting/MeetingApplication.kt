package com.taskforce.superinvention.app.domain.meeting

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import javax.persistence.*

@Entity
class MeetingApplication(
        @ManyToOne
        @JoinColumn(name = "club_user_seq")
        var clubUser: ClubUser,

        @ManyToOne
        @JoinColumn(name = "meeting_seq")
        var meeting: Meeting,
        var deleteFlag: Boolean
) : BaseEntity(){
}