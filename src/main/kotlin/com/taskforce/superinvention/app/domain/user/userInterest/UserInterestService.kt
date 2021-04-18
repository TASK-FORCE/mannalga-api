package com.taskforce.superinvention.app.domain.user.userInterest

import com.taskforce.superinvention.app.domain.interest.interest.InterestRepository
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.interest.InterestWithPriorityDto
import com.taskforce.superinvention.app.web.dto.interest.UserInterestDto
import com.taskforce.superinvention.app.web.dto.user.info.UserInfoInterestDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.NullPointerException

@Service
class UserInterestService (
        private val interestRepository: InterestRepository,
        private val userInterestRepository: UserInterestRepository
){
    @Transactional
    fun changeUserInterest(user: User, userInterests: List<InterestRequestDto>): UserInterestDto {
        val toDelete = userInterestRepository.findByUserOrderByPriority(user)
        userInterestRepository.deleteAll(toDelete)

        val toAdd: List<UserInterest> = userInterests.map { e ->
            val interest = interestRepository.findById(e.seq).orElseThrow{ NullPointerException() }
            UserInterest(user = user, interest = interest, priority = e.priority)
        }.toList()

        userInterestRepository.saveAll(toAdd)
        return findUserInterest(user)
    }

    @Transactional
    fun findUserInterest(user: User): UserInterestDto {
        val findByUser = userInterestRepository.findByUserOrderByPriority(user)
        return UserInterestDto(user.seq!!, user.userId, findByUser.map { e -> InterestWithPriorityDto(e) })
    }

    @Transactional
    fun findUserInterests(user: User): List<UserInfoInterestDto> {
        return userInterestRepository.findUserInterests(user)
                .map { userInterest -> UserInfoInterestDto(userInterest) }
    }
}