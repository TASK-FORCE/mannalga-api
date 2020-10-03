package com.taskforce.superinvention.app.domain.region

import com.fasterxml.jackson.annotation.JsonIgnore
import com.taskforce.superinvention.app.domain.BaseEntity
import javax.persistence.*

@Entity
class Region(
        @ManyToOne(fetch = FetchType.LAZY)
        @JsonIgnore
        var superRegion: Region?,
        var name: String,
        var superRegionRoot: String,
        var level: Long,

        @OneToMany(mappedBy = "superRegion", fetch = FetchType.LAZY)
        var subRegions: List<Region>
): BaseEntity()