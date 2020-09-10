package com.taskforce.superinvention.app.domain.role

import org.springframework.stereotype.Service

@Service
class RoleService(
        var roleRepository: RoleRepository,
        var roleGroupRepository: RoleGroupRepository
) {
    fun findByRole(roleName: Role.RoleName) :Role = roleRepository.findByName(roleName);

}