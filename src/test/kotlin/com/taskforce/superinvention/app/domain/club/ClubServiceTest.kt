package com.taskforce.superinvention.app.domain.club

import com.taskforce.superinvention.app.domain.interest.ClubInterest
import com.taskforce.superinvention.app.domain.interest.ClubInterestRepository
import com.taskforce.superinvention.app.domain.interest.interest.InterestService
import com.taskforce.superinvention.app.domain.state.ClubState
import com.taskforce.superinvention.app.domain.state.ClubStateRepository
import com.taskforce.superinvention.app.domain.state.StateRepository
import org.junit.Ignore
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.random.Random

@SpringBootTest
internal class ClubServiceTest {
    @Autowired
    lateinit var clubService: ClubService

    @Autowired
    lateinit var clubRepository: ClubRepository

    @Autowired
    lateinit var interestService: InterestService

    @Autowired
    lateinit var clubInterestRepository: ClubInterestRepository

    @Autowired
    lateinit var stateRepository: StateRepository

    @Autowired
    lateinit var clubStateRepository: ClubStateRepository

    /**
     * 모임 더미데이터 생성
     */
    @Test
    @Ignore
    fun addClub() {
        for (i in 0..100) {
            // generate club
            val club = clubRepository.save(Club(
                    name = "dummy club $i",
                    description = "dummy club description $i",
                    maximumNumber = 10L + i,
                    mainImageUrl = null
                )
            )

            // generate club interests
            val randomInterestSeq = Random.nextLong(1, 17)
            val interest = interestService.findBySeq(randomInterestSeq)
            val clubInterest = clubInterestRepository.save(ClubInterest(club, interest, 1))

            // generate club states
            val randomStateSeq = Random.nextLong(101, 123)
            val randomState = stateRepository.findById(randomStateSeq)
            val clubState = clubStateRepository.save(ClubState(club, randomState.get(), 1))

        }
    }
}


