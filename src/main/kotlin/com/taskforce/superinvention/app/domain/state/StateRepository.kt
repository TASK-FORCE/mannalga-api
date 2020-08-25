package com.taskforce.superinvention.app.domain.state

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
interface StateRepository : JpaRepository<State, Long>, StateRepositoryCustom

interface StateRepositoryCustom {

    fun findByLevel(level: Long): List<State>
}

@Repository
class StateRepositoryImpl : StateRepositoryCustom,
                            QuerydslRepositorySupport(State::class.java)  {

    override fun findByLevel(level: Long): List<State> {
        val state = QState.state
        return from(state)
                .where(state.level.eq(level))
                .fetch()
    }
}