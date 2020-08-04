package com.taskforce.superinvention.common.config.security

import com.taskforce.superinvention.app.domain.user.User

class SecurityUser: org.springframework.security.core.userdetails.User {

    val user: User
    val oAuthToken: String

    constructor(user: User, oAuthToken: String):
        super(user.userId, "", user.userRoles) {
        this.user = user
        this.oAuthToken = oAuthToken
    }
}