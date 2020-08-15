package com.taskforce.superinvention.app.web

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.ClubService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clubs")
class ClubController(
        val clubService : ClubService
) {

    @GetMapping("/{seq}")
    fun getClubInfoBySeq(@PathVariable seq : Long): Club?{
        return clubService.getClubInfo(seq)
    }

    @GetMapping
    fun retrieveClubs(@RequestParam("keyword") keyword : String): List<Club>?{
        return clubService.retrieveClubList(keyword)
    }
    
}