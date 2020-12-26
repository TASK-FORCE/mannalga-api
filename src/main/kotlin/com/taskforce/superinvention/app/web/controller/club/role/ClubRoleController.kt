package com.taskforce.superinvention.app.web.controller.club.role

import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.club.user.ClubUserService
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.ClubUserWithUserDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import com.taskforce.superinvention.common.exception.club.UserIsNotClubMemberException
import org.springframework.web.bind.annotation.*

@RestController("/clubs/{clubSeq}/roles")
class ClubRoleController(
        val roleService: RoleService,
        val clubService: ClubService,
        val clubUserService: ClubUserService

) {
    val userIsNotClubMemberException = UserIsNotClubMemberException()

    @GetMapping("/managers")
    fun getClubUsersHasManagerRole(
            @AuthUser user: User,
            @PathVariable clubSeq: Long
    ): List<ClubUserWithUserDto> {
        val clubUser = clubService.getClubUser(clubSeq, user) ?: throw userIsNotClubMemberException
        return clubService.getManagers(clubSeq)
    }

    @PostMapping("/managers")
    fun addManager(
            @AuthUser user: User,
            @PathVariable clubSeq: Long
    ) {
        val clubUser = clubService.getClubUser(clubSeq, user) ?: throw userIsNotClubMemberException

    }


    @DeleteMapping("/managers/{clubUserSeq}")
    fun deleteManager(
            @AuthUser user: User,
            @PathVariable clubSeq: Long,
            @PathVariable clubUserSeq: Long
    ) {
        val clubUser = clubService.getClubUser(clubSeq, user) ?: throw userIsNotClubMemberException

    }

    @PutMapping("/masters/{clubUserSeq}")
    fun changeClubMaster(
        @AuthUser user: User,
        @PathVariable clubSeq: Long,
        @PathVariable clubUserSeq: Long
    ) {
        val clubUser = clubService.getClubUser(clubSeq, user) ?: throw userIsNotClubMemberException

    }





}