package com.taskforce.superinvention.app.domain.interest.interestGroup

import com.querydsl.core.group.GroupBy.groupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.jpa.impl.JPAQueryFactory
import com.taskforce.superinvention.app.domain.interest.interest.Interest
import com.taskforce.superinvention.app.domain.interest.interest.InterestDto
import com.taskforce.superinvention.app.domain.interest.interest.QInterest.interest
import com.taskforce.superinvention.app.domain.interest.interestGroup.QInterestGroup.interestGroup
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository

@Repository
class InterestGroupRepositoryImpl(
    private val query: JPAQueryFactory
): InterestGroupRepositoryCustom,
   QuerydslRepositorySupport(InterestGroup::class.java) {

    override fun findAllInterestGroupList(): MutableList<InterestGroupDto> {

        val groups: MutableMap<InterestGroup, List<Interest>> = query.
                select(interestGroup.seq, interestGroup.name, interest.seq, interest.name).
                from(interestGroup).
                innerJoin(interestGroup.interesList, interest).
                transform(groupBy(interestGroup).`as`(list(interest)))

        return groups.map { interestGroup ->
            InterestGroupDto(interestGroup.key, interestGroup.value)
        }.toMutableList()
    }
}