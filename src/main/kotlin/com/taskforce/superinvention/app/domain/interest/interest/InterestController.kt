package com.taskforce.superinvention.app.domain.interest.interest

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class InterestController(
        private val interestService: InterestService
) {
}