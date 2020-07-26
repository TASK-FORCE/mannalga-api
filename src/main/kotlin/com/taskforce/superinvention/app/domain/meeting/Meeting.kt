package com.taskforce.superinvention.app.domain.meeting

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.Club
import java.time.LocalDateTime
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class Meeting(
        var title: String,
        var content: String,
        var startTimestamp: LocalDateTime,
        var endTimestamp: LocalDateTime,
        @ManyToOne
        var club: Club,
        var deleteFlag: Boolean
) : BaseEntity() {
}