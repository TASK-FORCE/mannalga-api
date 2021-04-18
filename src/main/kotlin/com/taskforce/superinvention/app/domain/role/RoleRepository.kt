package com.taskforce.superinvention.app.domain.role

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    fun findByName(roleName: Role.RoleName): Role
    fun findBySeqIn(roleSeqList: Set<Long>): Set<Role>
    fun findByNameIn(roleNameList: Set<Role.RoleName>): Set<Role>
}