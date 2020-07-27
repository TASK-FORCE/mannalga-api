package com.taskforce.superinvention.app.domain.state

import com.taskforce.superinvention.app.domain.BaseEntity
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class State(
        @ManyToOne
        var superState: State?,
        var name: String,
        var superStateRoot: String,
        var level: Long
): BaseEntity() {
}