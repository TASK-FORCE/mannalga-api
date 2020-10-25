package com.taskforce.superinvention.app.domain.region

import com.fasterxml.jackson.annotation.JsonIgnore
import com.taskforce.superinvention.app.domain.BaseEntity
import javax.persistence.*

@Entity
class Region(
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="super_region_seq")
        @JsonIgnore
        var superRegion: Region?,
        var name: String,
        var superRegionRoot: String,
        var level: Long,

        @OneToMany(mappedBy = "superRegion")
        var subRegions: List<Region>
): BaseEntity()