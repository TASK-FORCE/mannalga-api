package com.taskforce.superinvention.common.config.security

import com.taskforce.superinvention.app.domain.user.User
class SecurityUser {

    var user: User
    var oAuthToken: String

    constructor(user: User, oAuthToken: String) {
        this.user = user
        this.oAuthToken = oAuthToken
    }
}