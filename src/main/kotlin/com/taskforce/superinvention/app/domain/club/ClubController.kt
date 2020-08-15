package com.taskforce.superinvention.app.domain.club

import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clubs")
class ClubController(
        val clubService : ClubService
) {

    @GetMapping("/{seq}")
    fun getUserInfo(@PathVariable seq : Long): Club?{
        return clubService.retrieveClubInfo(seq)
    }

    @PostMapping("/BBB")
    fun registerClub() :String{
        return "hi"
    }
}