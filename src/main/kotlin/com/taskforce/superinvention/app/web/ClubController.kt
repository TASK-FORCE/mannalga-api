package com.taskforce.superinvention.app.web

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.user.user.User
import com.taskforce.superinvention.app.web.dto.club.ClubAddRequestDto
import com.taskforce.superinvention.app.web.dto.club.ClubUserDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("clubs")
class ClubController(
        val clubService : ClubService
) {
    @GetMapping
    fun retrieveClubs(@RequestParam("keyword") keyword : String): List<Club>?{
        return clubService.retrieveClubs(keyword)
    }

    @GetMapping("/{seq}")
    fun getClubBySeq(@PathVariable seq : Long): Club? {
        return clubService.getClubBySeq(seq)
    }

    @GetMapping("/{seq}/users")
    fun getClubUser(@PathVariable seq : Long): ClubUserDto? {
        return clubService.getClubUserList(seq)
    }

    @Secured("ROLE_USER")
    @PostMapping
    fun addClub(@AuthUser user: User, @RequestBody request: ClubAddRequestDto) {
        val club = Club(name = request.name, description = request.description, maximumNumber = request.maximumNumber)
        clubService.addClub(club, user)
    }
}