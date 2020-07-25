package com.taskforce.superinvention.app.domain.board

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.ClubUser
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class ClubBoard(
        var title: String,
        var content: String,
        @ManyToOne
        var clubUser: ClubUser,
        var topFixedFlag: Boolean,
        var deleteFlag: Boolean,
        var notificationFlag: Boolean
): BaseEntity() {
}