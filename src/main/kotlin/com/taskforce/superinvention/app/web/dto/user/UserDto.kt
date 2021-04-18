package com.taskforce.superinvention.app.web.dto.user

import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.UserType
import com.taskforce.superinvention.app.domain.user.userRole.UserRole
import com.taskforce.superinvention.app.web.dto.role.RoleDto
import com.taskforce.superinvention.common.util.extendFun.toBaseDate
import java.time.LocalDate
import javax.persistence.OneToMany

data class UserMemberCheckDto(
    val isMember: Boolean
)

data class UserDto(
        var seq: Long,
        var userRoles: Set<String>,
        var userName: String?,
        var birthday: String?,
        var profileImageLink: String?
) {
    constructor(user: User): this(
            seq = user.seq!!,
            userRoles = user.userRoles.map { it.roleName }.toSet(),
            userName = user.userName,
            birthday = user.birthday?.toBaseDate(),
            profileImageLink = user.profileImageLink
    )
}

data class UserIdAndNameDto(
    var seq: Long,
    var userName: String
) {
    constructor(user: User): this(user.seq!!, user.userName!!)
}