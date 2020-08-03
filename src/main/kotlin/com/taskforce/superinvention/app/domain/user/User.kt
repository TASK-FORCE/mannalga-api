package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.domain.BaseEntity
import javax.persistence.*

@Entity
class User: BaseEntity {

    var userId: String

    @Enumerated(EnumType.STRING)
    var userType: UserType

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    var userRoles: MutableSet<UserRole>

    constructor(userId: String, userType: UserType, userRoles: MutableSet<UserRole>) {
        this.userId = userId
        this.userType = userType
        this.userRoles = userRoles
    }

    constructor(userId: String, userType: UserType) {
        this.userId = userId
        this.userType = userType
        this.userRoles = hashSetOf()
    }

    constructor(userId: String) {
        this.userId = userId
        this.userType = UserType.KAKAO
        this.userRoles = hashSetOf()
    }
}