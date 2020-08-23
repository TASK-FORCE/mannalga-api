package com.taskforce.superinvention.app.domain.state

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
interface UserStateRepository : JpaRepository<UserState, Long>,
                                UserStateRepositoryCustom

interface UserStateRepositoryCustom {
    fun findByUserSeq(userSeq: Long): List<UserState>
}

@Repository
class UserStateRepositoryImpl : UserStateRepositoryCustom,
                                QuerydslRepositorySupport(UserState::class.java) {

    override fun findByUserSeq(userSeq: Long): List<UserState> {
        val userState = QUserState.userState

        return from(userState)
                .where(userState.user.seq.eq(userSeq))
                .orderBy(userState.priority.asc())
                .fetch()
    }
}