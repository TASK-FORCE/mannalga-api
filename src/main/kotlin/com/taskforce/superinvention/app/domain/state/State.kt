package com.taskforce.superinvention.app.domain.state

import com.fasterxml.jackson.annotation.JsonIgnore
import com.taskforce.superinvention.app.domain.BaseEntity
import javax.persistence.*

@Entity
class State(
        @ManyToOne(fetch = FetchType.LAZY)
        @JsonIgnore
        var superState: State?,
        var name: String,
        var superStateRoot: String,
        var level: Long,

        @OneToMany(mappedBy = "superState", fetch = FetchType.LAZY)
        var subStates: List<State>
): BaseEntity()