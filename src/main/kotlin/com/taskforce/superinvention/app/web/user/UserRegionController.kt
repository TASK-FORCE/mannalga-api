package com.taskforce.superinvention.app.web.user

import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.userRegion.UserRegionService
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto
import com.taskforce.superinvention.app.web.dto.region.UserRegionDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/users/regions")
class UserRegionController(
        private val userRegionService: UserRegionService
) {
    @Secured(Role.NONE, Role.MEMBER)
    @GetMapping
    fun getUserRegionList(@AuthUser user: User): UserRegionDto {
        val findUserRegionList = userRegionService.findUserRegionList(user)
        return findUserRegionList
    }

    @Secured(Role.NONE, Role.MEMBER)
    @PutMapping
    fun changeUserRegion(@AuthUser user: User,
                         @RequestBody regionRequestDto: List<RegionRequestDto>): UserRegionDto {
        return userRegionService.changeUserRegion(user, regionRequestDto)
    }
}

