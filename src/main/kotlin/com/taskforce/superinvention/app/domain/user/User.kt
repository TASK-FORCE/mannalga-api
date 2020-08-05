package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.web.dto.kakao.KakaoToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import javax.persistence.*

@Entity
class User: BaseEntity, UserDetails {

    var userId: String

    @Enumerated(EnumType.STRING)
    var userType: UserType

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    var userRoles: MutableSet<UserRole>

    var accessToken: String?  = ""
    var refrestToken: String? = ""

    constructor(userId: String, userType: UserType, userRoles: MutableSet<UserRole>) {
        this.userId = userId
        this.userType = userType
        this.userRoles = userRoles
    }

    constructor(userId: String) {
        this.userId = userId
        this.userType =  UserType.KAKAO
        this.userRoles = hashSetOf()
    }

    constructor(userId: String, token: KakaoToken) {
        this.userId = userId
        this.userType = UserType.KAKAO
        this.userRoles = hashSetOf()
        this.accessToken = token.access_token
        this.refrestToken = token.refresh_token
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return this.userRoles
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun getUsername(): String {
        return userId
    }

    override fun isCredentialsNonExpired(): Boolean {
       return true
    }

    override fun getPassword(): String {
        return ""
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }
}