package com.taskforce.superinvention.app.domain.user

interface UserRepositoryCustom {
    fun findByUserId(id: String): User?
}