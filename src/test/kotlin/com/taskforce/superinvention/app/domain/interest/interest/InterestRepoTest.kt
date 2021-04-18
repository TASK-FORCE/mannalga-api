package com.taskforce.superinvention.app.domain.interest.interest

import com.taskforce.superinvention.config.test.DataJpaRepoTest
import org.springframework.beans.factory.annotation.Autowired

class InterestRepoTest: DataJpaRepoTest() {

    @Autowired
    lateinit var interestRepository: InterestRepository
}