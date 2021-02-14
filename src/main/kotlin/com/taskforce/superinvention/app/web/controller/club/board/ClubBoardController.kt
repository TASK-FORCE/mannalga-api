package com.taskforce.superinvention.app.web.controller.club.board

import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.app.domain.club.board.ClubBoardService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardDto
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardRegisterBody
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardListViewDto
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardSearchOpt
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.common.config.argument.resolver.auth.AuthUser
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
    @GetMapping("/{clubSeq}/board")
    fun getClubBoardList(pageable: Pageable,
                         @PathVariable clubSeq: Long,
                         @RequestParam(required = false) category: ClubBoard.Category?,
                         searchRequest: ClubBoardSearchOpt): ResponseDto<PageDto<ClubBoardListViewDto>> {

        val search = clubBoardService.getClubBoardList(pageable, category, searchRequest, clubSeq)
        return ResponseDto(data = search)
    }

    /**
     * 모임 게시판 글 단건 조회
     */
    @GetMapping("/{clubSeq}/board/{boardSeq}")
    fun getClubBoard(@AuthUser user: User?,
                     @PathVariable clubSeq : Long,
                     @PathVariable boardSeq: Long,
                     pageable: Pageable,
                     searchRequest: ClubBoardSearchOpt): ResponseDto<ClubBoardDto> {

        val search = clubBoardService.getClubBoard(user, boardSeq, clubSeq)
        return ResponseDto(data = search)
    }


    /**
     * 모임 게시판 글 등록
     */
    @PostMapping("/{clubSeq}/board")
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
    @DeleteMapping("/{clubBoardSeq}/board")
    fun deleteClubBoard(@AuthUser user: User, @PathVariable clubBoardSeq: Long): ResponseDto<String> {

        clubBoardService.deleteClubBoard(user, clubBoardSeq)
        return ResponseDto(data = "")
    }
}