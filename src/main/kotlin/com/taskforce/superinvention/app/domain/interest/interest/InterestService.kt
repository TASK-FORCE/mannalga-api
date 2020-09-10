package com.taskforce.superinvention.app.domain.interest.interest

import com.taskforce.superinvention.app.domain.user.userInterest.UserInterestRepository
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException

@Service
class InterestService(
        private val interestRepository: InterestRepository,
        private val userInterestRepository: UserInterestRepository
) {
    fun findBySeq(seq: Long):Interest = interestRepository.findById(seq).orElseThrow{IllegalArgumentException()}
}