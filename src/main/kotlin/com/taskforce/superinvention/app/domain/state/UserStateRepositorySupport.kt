package com.taskforce.superinvention.app.domain.state

import com.querydsl.jpa.impl.JPAQueryFactory
import com.taskforce.superinvention.app.domain.state.QUserState.*
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class UserStateRepositorySupport(
        var queryFactory: JPAQueryFactory
) : QuerydslRepositorySupport(UserState::class.java) {

    fun findByUserSeq(userSeq: Long):List<UserState> =
            queryFactory.selectFrom(userState).where(userState.user.seq.eq(userSeq)).fetch()

}