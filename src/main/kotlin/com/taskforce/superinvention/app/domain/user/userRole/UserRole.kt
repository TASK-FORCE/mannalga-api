package com.taskforce.superinvention.app.domain.user.userRole

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.user.User
import org.springframework.security.core.GrantedAuthority
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class UserRole(
        @ManyToOne
        @JoinColumn(name = "user_seq")
        var user: User,
        var roleName: String
) : BaseEntity(), GrantedAuthority {

    override fun getAuthority(): String {
        return roleName
    }
}