package com.taskforce.superinvention.app.domain.region

import com.fasterxml.jackson.annotation.JsonIgnore
import com.taskforce.superinvention.app.domain.BaseEntity
import javax.persistence.*

@Entity
class Region(

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name="super_region_seq", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
        @JsonIgnore
        var superRegion: Region?,
        var name: String,
        var superRegionRoot: String,
        var level: Long

): BaseEntity() {

        @OneToMany
        @JoinColumn(name="super_region_seq", foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
        var subRegions: List<Region> = listOf()
}