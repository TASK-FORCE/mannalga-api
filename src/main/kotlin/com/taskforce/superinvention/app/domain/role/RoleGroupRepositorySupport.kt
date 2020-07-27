package com.taskforce.superinvention.app.domain.role

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class RoleGroupRepositorySupport : QuerydslRepositorySupport(RoleGroup::class.java){
}