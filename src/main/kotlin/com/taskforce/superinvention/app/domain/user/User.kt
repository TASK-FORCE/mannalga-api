package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.domain.BaseEntity
import javax.persistence.*

@Entity
class User: BaseEntity {

    var userId: String

    @Enumerated(EnumType.STRING)
    var userType: UserType

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    var userRoles: Set<UserRole>

    constructor(userId: String, userType: UserType, userRoles: Set<UserRole>) {
        this.userId = userId
        this.userType = userType
        this.userRoles = userRoles
    }

    constructor(userId: String, userType: UserType) {
        this.userId = userId
        this.userType = userType
        this.userRoles = emptySet()
    }

//    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
//        return this.userRoles.stream().map { role -> SimpleGrantedAuthority("ROLE_$role") }.collect(Collectors.toSet())
//    }
//
//    override fun isEnabled(): Boolean {
//        return true
//    }
//
//    override fun getUsername(): String {
//        return "";
//    }
//
//    override fun isCredentialsNonExpired(): Boolean {
//        return true
//    }
//
//    override fun getPassword(): String {
//        TODO("Not yet implemented")
//    }
//
//    override fun isAccountNonExpired(): Boolean {
//        return true
//    }
//
//    override fun isAccountNonLocked(): Boolean {
//        return true
//    }
}