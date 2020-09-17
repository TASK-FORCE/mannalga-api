package com.taskforce.superinvention.app.domain.user

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.taskforce.superinvention.app.domain.BaseEntity
import com.taskforce.superinvention.app.domain.user.UserType
import com.taskforce.superinvention.app.domain.user.userRole.UserRole
import com.taskforce.superinvention.app.web.dto.kakao.KakaoToken
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
    var refreshToken: String? = ""

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
        this.refreshToken = token.refresh_token
    }
}