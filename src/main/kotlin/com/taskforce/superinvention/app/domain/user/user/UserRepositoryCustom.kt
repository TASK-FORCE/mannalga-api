package com.taskforce.superinvention.app.domain.user.user

interface UserRepositoryCustom {
    fun findByUserId(id: String): User?
}