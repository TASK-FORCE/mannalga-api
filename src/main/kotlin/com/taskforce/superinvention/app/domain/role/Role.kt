package com.taskforce.superinvention.app.domain.role

import com.taskforce.superinvention.app.domain.BaseEntity
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class Role (
        var name: String,
        @ManyToOne
        var roleGroup: RoleGroup
): BaseEntity() {
}