package com.taskforce.superinvention.app.domain.user.user

import com.taskforce.superinvention.app.domain.user.user.QUser.user
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class UserRepositorySupport : UserRepositoryCustom,
                              QuerydslRepositorySupport(User::class.java) {

    override fun findByUserId(id: String): User {
        return from(user)
                .where(user.userId.eq(id))
                .fetchOne()
    }
}