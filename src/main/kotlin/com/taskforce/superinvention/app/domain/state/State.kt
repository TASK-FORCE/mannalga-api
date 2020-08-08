package com.taskforce.superinvention.app.domain.state

import com.fasterxml.jackson.annotation.JsonIgnore
import com.taskforce.superinvention.app.domain.BaseEntity
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.ManyToOne
import javax.persistence.OneToMany

@Entity
class State(
        @ManyToOne(fetch = FetchType.LAZY)
        @JsonIgnore
        var superState: State?,
        var name: String,
        var superStateRoot: String,
        var level: Long,

        @OneToMany(mappedBy = "superState", fetch = FetchType.EAGER)
        var subStates: List<State>
): BaseEntity() {
}