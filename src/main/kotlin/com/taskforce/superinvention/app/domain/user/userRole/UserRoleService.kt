package com.taskforce.superinvention.app.domain.user.userRole

import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.user.User
import org.springframework.stereotype.Service

@Service
class UserRoleService(
        val userRoleRepository: UserRoleRepository
) {

    fun addRole(user: User, roleName: Role.RoleName) {
        userRoleRepository.save(UserRole(user, roleName.role))
    }

    fun removeRoleIfExist(user: User, roleName: Role.RoleName) {
        val userRole = userRoleRepository.findByUserSeqAndRoleName(user.seq!!, roleName.role)

        if(userRole != null) {
            userRoleRepository.delete(userRole)
        }
    }
}