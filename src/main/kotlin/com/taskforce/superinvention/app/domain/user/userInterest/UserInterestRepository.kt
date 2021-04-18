package com.taskforce.superinvention.app.domain.user.userInterest

import com.taskforce.superinvention.app.domain.interest.interest.QInterest
import com.taskforce.superinvention.app.domain.interest.interestGroup.QInterestGroup
import com.taskforce.superinvention.app.domain.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
interface UserInterestRepository : JpaRepository<UserInterest, Long>, UserInterestRepositoryCustom{
    fun findByUserOrderByPriority(user: User): List<UserInterest>
}

interface UserInterestRepositoryCustom {
    fun findUserInterests(user: User): List<UserInterest>
}

@Repository
class UserInterestRepositoryImpl: UserInterestRepositoryCustom,
                                  QuerydslRepositorySupport(UserInterest::class.java) {

    override fun findUserInterests(user: User): List<UserInterest> {
        val userInterest = QUserInterest.userInterest
        val interest = QInterest.interest
        val interestGroup = QInterestGroup.interestGroup

        val query = from(userInterest)
                .join(userInterest.interest, interest).fetchJoin()
                .join(interest.interestGroup, interestGroup).fetchJoin()
                .where(userInterest.user.seq.eq(user.seq))

        return query.fetch()
    }
}