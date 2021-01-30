package com.taskforce.superinvention.app.web.controller.club

import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.app.domain.club.board.ClubBoardService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardRegisterBody
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardListViewDto
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardSearchOpt
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
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
                         searchRequest: ClubBoardSearchOpt): ResponseDto<PageDto<ClubBoardListViewDto>> {

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
                          @RequestBody body: ClubBoardRegisterBody): ResponseDto<String> {

        clubBoardService.registerClubBoard(user, clubSeq, body)
        return ResponseDto(data = "")
    }

    /**
     * 모임 게시판 글 삭제
     */
    @DeleteMapping("/{clubBoardSeq}/boards")
    fun deleteClubBoard(@AuthUser user: User, @PathVariable clubBoardSeq: Long): ResponseDto<String> {

        clubBoardService.deleteClubBoard(user, clubBoardSeq)
        return ResponseDto(data = "")
    }
}