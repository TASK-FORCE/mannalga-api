package com.taskforce.superinvention.app.domain.region

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.region.Region
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne

@Entity
class ClubRegion(
        @ManyToOne(fetch = FetchType.LAZY)
        var club:Club,

        @ManyToOne(fetch = FetchType.LAZY)
        var region: Region,
        var priority: Long
) : BaseEntity()