package com.taskforce.superinvention.app.web

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.ClubAddRequestDto
import com.taskforce.superinvention.app.web.dto.club.ClubUserDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.security.access.prepost.PreAuthorize
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

    @PostMapping("/{clubSeq}/users")
    fun addClubUser(@AuthUser user: User, @PathVariable("clubSeq") clubSeq: Long) {
        val club = clubService.getClubBySeq(clubSeq)
        if (club == null) throw NullPointerException("존재하지 않는 모임입니다")
        clubService.addClubUser(club, user);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    fun addClub(@AuthUser user:User, @RequestBody request: ClubAddRequestDto) {
        val club = Club(name = request.name, description = request.description, maximumNumber = request.maximumNumber)
        clubService.addClub(club, user)
    }

}