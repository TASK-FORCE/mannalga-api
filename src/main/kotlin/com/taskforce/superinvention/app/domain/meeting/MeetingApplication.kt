package com.taskforce.superinvention.app.domain.meeting

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.ClubUser
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class MeetingApplication(
        @ManyToOne
        var clubUser: ClubUser,
        @ManyToOne
        var meeting: Meeting,
        var deleteFlag: Boolean
) : BaseEntity(){
}