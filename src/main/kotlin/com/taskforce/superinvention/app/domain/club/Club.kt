package com.taskforce.superinvention.app.domain.club

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.interest.ClubInterest
import com.taskforce.superinvention.app.domain.region.ClubRegion
import org.hibernate.annotations.Formula
import javax.persistence.*

@Entity
@JsonIdentityInfo(property = "objId", generator = ObjectIdGenerators.StringIdGenerator::class)
class Club(
    var name          : String,
    var description   : String,
    var maximumNumber : Long,
    var mainImageUrl  : String?
): BaseEntity() {

    @Formula("(select count(*) from club_user cu where cu.club_seq = seq)")
    var userCount: Long ?= null

    @OneToMany
    @JoinColumn(name = "club_seq")
    @OrderBy("priority")
    lateinit var clubInterests: Set<ClubInterest>

    @OneToMany
    @JoinColumn(name = "club_seq")
    @OrderBy("priority")
    lateinit var clubRegions: Set<ClubRegion>

    @OneToMany
    @JoinColumn(name = "club_seq")
    lateinit var clubUser: Set<ClubUser>

}