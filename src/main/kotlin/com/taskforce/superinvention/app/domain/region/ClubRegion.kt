package com.taskforce.superinvention.app.domain.region

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.Club
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity(name = "club_region")
class ClubRegion(
        @ManyToOne
        var club:Club,
        @ManyToOne
        var region: Region,
        var priority: Long
) : BaseEntity()