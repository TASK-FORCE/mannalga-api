package com.taskforce.superinvention.app.web

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.web.dto.club.ClubUserDto
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clubs")
class ClubController(
        val clubService : ClubService
) {

    @GetMapping
    fun getAllClubs(): List<Club>? {
        return clubService.getAllClubs()
    }

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

}