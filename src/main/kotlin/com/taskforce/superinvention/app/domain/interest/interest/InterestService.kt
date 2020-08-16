package com.taskforce.superinvention.app.domain.interest.interest

import com.taskforce.superinvention.app.domain.interest.UserInterest
import com.taskforce.superinvention.app.domain.interest.UserInterestRepository
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class InterestService(
        private val interestRepository: InterestRepository,
        private val userInterestRepository: UserInterestRepository
) {

    @Transactional
    fun changeUserInterest(user: User, userInterests: List<InterestRequestDto>) {
        val toDelete = userInterestRepository.findByUser(user)
        userInterestRepository.deleteAll(toDelete)

        val toAdd: List<UserInterest> = userInterests.map { e ->
                val interest = interestRepository.findById(e.interestSeq)
                UserInterest(user = user, interest = interest, priority = e.priority)
        }.toList()

        userInterestRepository.saveAll(toAdd)
    }
}