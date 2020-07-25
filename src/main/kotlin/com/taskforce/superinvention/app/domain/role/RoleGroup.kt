package com.taskforce.superinvention.app.domain.role

import com.taskforce.superinvention.app.domain.BaseEntity
import javax.persistence.Entity

@Entity
class RoleGroup(
    var name: String,
    var role_type: String
) : BaseEntity() {

}