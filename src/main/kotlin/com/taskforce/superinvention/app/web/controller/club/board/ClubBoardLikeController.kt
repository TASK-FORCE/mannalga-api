package com.taskforce.superinvention.app.web.controller.club.board

import com.taskforce.superinvention.app.domain.club.board.like.ClubBoardLikeService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.club.board.like.ClubBoardLikeDto
import com.taskforce.superinvention.common.config.argument.resolver.auth.AuthUser
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/club/{clubSeq}/board/{clubBoardSeq}/like")
class ClubBoardLikeController(
        private val clubBoardLikeService: ClubBoardLikeService
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun registerClubBoardLike(@AuthUser user: User,
                              @PathVariable clubSeq: Long,
                              @PathVariable clubBoardSeq: Long): ResponseDto<ClubBoardLikeDto> {

        return ResponseDto(data = clubBoardLikeService.registerClubBoardLike(user, clubSeq, clubBoardSeq))
    }

    @DeleteMapping
    fun deleteClubBoardLike(@AuthUser user: User,
                              @PathVariable clubSeq: Long,
                              @PathVariable clubBoardSeq: Long): ResponseDto<ClubBoardLikeDto> {

        return ResponseDto(data = clubBoardLikeService.removeClubBoardLike(user, clubSeq, clubBoardSeq))
    }
}