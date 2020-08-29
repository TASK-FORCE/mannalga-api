package com.taskforce.superinvention.app.domain.user

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.user.userRole.UserRole
import com.taskforce.superinvention.app.web.dto.kakao.KakaoToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.LocalDate
import javax.persistence.*

@Entity
@JsonIdentityInfo(property = "userId", generator = ObjectIdGenerators.StringIdGenerator::class)
class User: BaseEntity {

    var userId: String

    @Enumerated(EnumType.STRING)
    var userType: UserType

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    var userRoles: MutableSet<UserRole>

    var accessToken: String?  = ""
    var refrestToken: String? = ""

    var userName: String? = ""

    var birthday: LocalDate? = null

    var profileImageLink: String? = null

    var isRegistered: Int? =0

    constructor(userId: String, userType: UserType, userRoles: MutableSet<UserRole>, userName:String, birthday: LocalDate) {
        this.userId = userId
        this.userType = userType
        this.userRoles = userRoles
        this.userName = userName
        this.birthday = birthday
    }

    constructor(userId: String) {
        this.userId = userId
        this.userType = UserType.KAKAO
        this.userRoles = hashSetOf()
    }

    constructor(userId: String, token: KakaoToken) {
        this.userId = userId
        this.userType = UserType.KAKAO
        this.userRoles = hashSetOf()
        this.accessToken = token.access_token
        this.refrestToken = token.refresh_token
    }
}