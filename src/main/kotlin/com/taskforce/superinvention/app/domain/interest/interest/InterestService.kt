package com.taskforce.superinvention.app.domain.interest.interest

import com.taskforce.superinvention.app.domain.user.userInterest.UserInterestRepository
import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalArgumentException

@Service
class InterestService(
        private val interestRepository: InterestRepository,
        private val userInterestRepository: UserInterestRepository
) {
    fun findBySeq(seq: Long):Interest = interestRepository.findById(seq).orElseThrow{IllegalArgumentException()}

    @Transactional
    fun checkBeforeConvertClubInterest(interestList: Iterable<Interest>): Iterable<Interest> {
        if (!isInEqualsInterestGroup(interestList)) throw BizException("하나의 관심사 그룹에 속하는 관심사들만 등록할 수 있습니다", HttpStatus.BAD_REQUEST)
        return interestList
    }

    @Transactional
    fun isInEqualsInterestGroup(interests: Iterable<Interest>): Boolean {
        return interests.map { it.interestGroup }.distinctBy { it.seq }.size == 1
    }

    @Transactional
    fun findBySeqList(seqList: Iterable<Long>): List<Interest> = interestRepository.findAllById(seqList)


}