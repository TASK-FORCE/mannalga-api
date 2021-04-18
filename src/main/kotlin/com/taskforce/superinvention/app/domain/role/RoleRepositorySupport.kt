package com.taskforce.superinvention.app.domain.role

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class RoleRepositorySupport : QuerydslRepositorySupport(Role::class.java)