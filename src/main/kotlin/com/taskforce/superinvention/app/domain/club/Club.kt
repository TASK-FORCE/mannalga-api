package com.taskforce.superinvention.app.domain.club

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.interest.ClubInterest
import com.taskforce.superinvention.app.domain.state.ClubState
import javax.persistence.*

@Entity
@JsonIdentityInfo(property = "seq", generator = ObjectIdGenerators.StringIdGenerator::class)
class Club(
    var name: String,
    var description: String,
    var maximumNumber: Long,
    var mainImageUrl: String?
) : BaseEntity() {
    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_seq")
    @OrderBy("priority")
    lateinit var clubInterests: List<ClubInterest>

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_seq")
    @OrderBy("priority")
    lateinit var clubStates: List<ClubState>

    @OneToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_seq")
    lateinit var clubUser: List<ClubUser>
}