package com.taskforce.superinvention.app.domain.role

import com.taskforce.superinvention.app.domain.BaseEntity
import javax.persistence.*

@Entity
class Role (
        @Enumerated(EnumType.STRING)
        var name: RoleName,

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "role_group_seq")
        var roleGroup: RoleGroup
): BaseEntity() {

        companion object {
                const val NONE        = "ROLE_NONE"
                const val MEMBER      = "ROLE_MEMBER"
                const val CLUB_MEMBER = "ROLE_CLUB_MEMBER"
                const val MANAGER     = "ROLE_MANAGER"
                const val MASTER      = "ROLE_MASTER"

                private val lookup = RoleName.values().associateBy(RoleName::role)
                fun fromRoleName(role: String): RoleName = requireNotNull(lookup[role])
        }

        enum class RoleName(
                val label: String,
                val role : String
        ) {
                NONE("비회원", Role.NONE),
                MEMBER("회원", Role.MEMBER),
                CLUB_MEMBER("모임원", Role.CLUB_MEMBER),
                MANAGER("매니저", Role.MANAGER),
                MASTER("모임장",Role.MASTER)
        }
}