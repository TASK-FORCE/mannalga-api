package com.taskforce.superinvention.app.domain.user

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import javax.transaction.Transactional

@Repository
class UserRepositorySupport : QuerydslRepositorySupport(User::class.java) {

    @Transactional
    fun findById(id: String): User {
        return from(QUser.user)
                .where(QUser.user.id.eq(id))
                .fetchOne()
    }

}