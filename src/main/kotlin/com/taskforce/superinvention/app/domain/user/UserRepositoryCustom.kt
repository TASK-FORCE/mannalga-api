package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.domain.user.User

interface UserRepositoryCustom {
    fun findByUserId(id: String): User?
}