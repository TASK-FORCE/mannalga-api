package com.taskforce.superinvention.config

import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.role.RoleGroup
import org.mockito.Mockito

object MockitoHelper {
    fun <T> anyObject(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> uninitialized(): T =  null as T

    fun getRoleByRoleName(roleName: Role.RoleName, level: Int): Role {
        return Role(roleName, RoleGroup("MOCK_ROLE_GROUP", "MOCK_ROLE_TYPE"), level)
    }
}