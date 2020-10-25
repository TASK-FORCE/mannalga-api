package com.taskforce.superinvention.app.domain.user.userRegion

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.region.Region
import com.taskforce.superinvention.app.domain.user.User
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class UserRegion(
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="user_seq")
        var user: User,
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="region_seq")
        var region: Region,
        var priority: Long
) : BaseEntity()