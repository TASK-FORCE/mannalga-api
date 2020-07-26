package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.domain.BaseEntity
import javax.persistence.Entity
import javax.persistence.ManyToOne

@Entity
class UserRole : BaseEntity {

    @ManyToOne
    var user: User

    var roleName: String

    constructor(user: User, roleName: String) {
        this.user = user
        this.roleName = roleName
    }
}