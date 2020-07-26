package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.domain.BaseEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.stream.Collectors
import javax.persistence.*
import kotlin.reflect.typeOf

@Entity
class User : UserDetails, BaseEntity{

    private var id: String
    private var name: String
    private var nickname: String

    @Enumerated(EnumType.STRING)
    private var userType: UserType

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private var userRoles: Set<UserRole>

    constructor(id: String, name: String, nickname: String, userType: UserType, userRoles: Set<UserRole>) {
        this.id = id
        this.name = name
        this.nickname = nickname
        this.userType = userType
        this.userRoles = userRoles
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return this.userRoles.stream().map { role -> SimpleGrantedAuthority("ROLE_$role") }.collect(Collectors.toSet())
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getUsername(): String {
        return name;
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun getPassword(): String {
        TODO("Not yet implemented")
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

}