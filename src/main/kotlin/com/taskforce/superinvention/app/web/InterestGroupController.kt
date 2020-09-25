package com.taskforce.superinvention.app.web

import com.taskforce.superinvention.app.domain.interest.interestGroup.InterestGroupDto
import com.taskforce.superinvention.app.domain.interest.interestGroup.InterestGroupService
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class InterestGroupController(
        private val interestGroupService: InterestGroupService
) {

    @GetMapping("/interestGroup/all")
    fun getInterestList(): ResponseDto<List<InterestGroupDto>> {
        val result = interestGroupService.getInterestList()
        return ResponseDto(data = result)
    }
}