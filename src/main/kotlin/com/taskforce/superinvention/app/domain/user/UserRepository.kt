package com.taskforce.superinvention.app.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long>, UserRepositoryCustom

interface UserRepositoryCustom {
    fun findByUserId(id: String): User?
}

@Repository
class UserRepositoryImpl : UserRepositoryCustom,
        QuerydslRepositorySupport(User::class.java) {

    override fun findByUserId(id: String): User {

        val user = QUser.user
        return from(user)
                .where(user.userId.eq(id))
                .fetchOne()
    }
}