package com.taskforce.superinvention.app.domain.user

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class UserRepositorySupport : UserRepositoryCustom,
                              QuerydslRepositorySupport(User::class.java) {

    override fun findByUserId(id: String): User {
        return from(QUser.user)
                .where(QUser.user.userId.eq(id))
                .fetchOne()
    }
}