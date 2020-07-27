package com.taskforce.superinvention.app.domain.user

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class UserRoleRepositorySupport : QuerydslRepositorySupport(UserRole::class.java) {
}