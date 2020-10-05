package com.taskforce.superinvention.app.web.controller.club

import com.taskforce.superinvention.app.domain.club.board.ClubBoardService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardBody
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardDto
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardPreviewDto
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardSearchOpt
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clubs")
class ClubBoardController(
        private val clubBoardService: ClubBoardService
) {

    /**
     * 모임 게시판 글 조회
     */
    @GetMapping("/{clubSeq}/boards")
    fun getClubBoardList(@PathVariable clubSeq: Long,
                         pageable: Pageable,
                         searchRequest: ClubBoardSearchOpt): ResponseDto<Page<ClubBoardPreviewDto>> {

        val search = clubBoardService.getClubBoardList(pageable, searchRequest, clubSeq)
        return ResponseDto(data = search)
    }

    /**
     * 모임 게시판 글 등록
     */
    @PostMapping("/{clubSeq}/boards")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerClubBoard(@AuthUser user: User,
                          @PathVariable clubSeq: Long,
                          @RequestBody  body: ClubBoardBody): ResponseDto<Any> {

        clubBoardService.registerClubBoard(user, clubSeq, body)
        return ResponseDto(data = "")
    }
}