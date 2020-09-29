package com.taskforce.superinvention.app.domain.user.userRegion

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.region.Region
import com.taskforce.superinvention.app.domain.user.User
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class UserRegion(
        @ManyToOne
        var user: User,
        @ManyToOne
        var region: Region,
        var priority: Long
) : BaseEntity()