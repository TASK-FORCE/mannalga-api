package com.taskforce.superinvention.app.domain.role

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.ClubUser
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class ClubUserRole(
        @ManyToOne
        var clubUser: ClubUser,
        @ManyToOne
        var role: Role
): BaseEntity()