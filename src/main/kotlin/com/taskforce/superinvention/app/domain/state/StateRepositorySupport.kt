package com.taskforce.superinvention.app.domain.state

import com.querydsl.jpa.impl.JPAQueryFactory
import com.taskforce.superinvention.app.domain.state.QState.state
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class StateRepositorySupport(
        var queryFactory: JPAQueryFactory
) : QuerydslRepositorySupport(State::class.java) {

    fun findByLevel(level: Long): List<State> {
        return queryFactory.select(state)
                .from(state)
                .where(state.level.eq(1))
                .fetch()
    }
}