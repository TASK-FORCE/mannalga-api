package com.taskforce.superinvention.app.web.controller.user

import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.userInterest.UserInterestService
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.interest.UserInterestDto
import com.taskforce.superinvention.common.config.argument.resolver.auth.AuthUser
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/interests")
class UserInterestController(
        private val userInterestService: UserInterestService
) {
    @Secured(Role.MEMBER)
    @GetMapping
    fun getUserInterestList(@AuthUser user: User): ResponseDto<UserInterestDto> {
        val findUserInterest: UserInterestDto = userInterestService.findUserInterest(user)
        return ResponseDto(data = findUserInterest)
    }

    @Secured(Role.MEMBER)
    @PutMapping
    fun changeUserInterest(@AuthUser user: User,
                           @RequestBody interestRequestDto: List<InterestRequestDto>): ResponseDto<UserInterestDto> {
        return  ResponseDto(data = userInterestService.changeUserInterest(user, interestRequestDto))
    }
}

