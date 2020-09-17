package com.taskforce.superinvention.app.web.controller.club

import com.taskforce.superinvention.app.domain.club.board.ClubBoardService
import com.taskforce.superinvention.app.domain.user.user.User
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clubs/")
class ClubBoardController(
        private val clubBoardService: ClubBoardService
) {

    @GetMapping("/{clubSeq}/boards")
    fun getClubBoardList() {

    }

    @PostMapping("/{clubSeq}/boards")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerClubBoard(@AuthUser user: User,
                          @PathVariable clubSeq: Long,
                          @RequestBody body: ClubBoardDto): ResponseDto<Any>{

        clubBoardService.registerClubBoard(user, clubSeq, body)
        return ResponseDto(data = "")
    }
}