package com.taskforce.superinvention.app.domain.club.board

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.ClubUser
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class ClubBoard(
        var title: String,
        var content: String,
        @ManyToOne
        @JoinColumn(name = "user_seq")
        var clubUser: ClubUser,
        var topFixedFlag: Boolean,
        var deleteFlag: Boolean,
        var notificationFlag: Boolean
): BaseEntity()