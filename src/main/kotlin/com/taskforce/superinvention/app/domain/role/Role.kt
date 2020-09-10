package com.taskforce.superinvention.app.domain.role

import com.taskforce.superinvention.app.domain.BaseEntity
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.ManyToOne

@Entity
class Role (
        @Enumerated(EnumType.STRING)
        var name: RoleName,
        @ManyToOne
        var roleGroup: RoleGroup
): BaseEntity() {
        enum class RoleName(
                label: String
        ) {
                NONE("비회원"),
                MEMBER("회원"),
                CLUB_MEMBER("모임원"),
                MANAGER("매니저"),
                MASTER("모임장")
        }
}