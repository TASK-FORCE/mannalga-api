package com.taskforce.superinvention.app.web.controller.club.board

import com.taskforce.superinvention.app.domain.club.board.comment.ClubBoardCommentService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.club.board.comment.ClubBoardCommentListDto
import com.taskforce.superinvention.app.web.dto.club.board.comment.ClubBoardCommentRegisterDto
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/club/{clubSeq}/board/{clubBoardSeq}")
class ClubBoardCommentController(
        private val clubBoardCommentService: ClubBoardCommentService
) {

    // 게시판 댓글 조회
    @GetMapping("/comment")
    fun getClubBoardComments(pageable: Pageable,
                             @AuthUser     user        : User,
                             @PathVariable clubSeq     : Long,
                             @PathVariable clubBoardSeq: Long) : ResponseDto<PageDto<ClubBoardCommentListDto>> {

        return ResponseDto(clubBoardCommentService.getCommentList(user, pageable, clubBoardSeq))
    }

    // 게시판 대댓글 조회
    @GetMapping("/comment/{parentCommentSeq}")
    fun getClubBoardComments(@AuthUser     user        : User,
                             @PathVariable clubSeq     : Long,
                             @PathVariable clubBoardSeq: Long,
                             @PathVariable parentCommentSeq: Long,
                             @RequestParam(required = false) depthLimit: Long?): ResponseDto<List<ClubBoardCommentListDto>> {

        return ResponseDto(clubBoardCommentService.getChildCommentList(user, parentCommentSeq, depthLimit ?: 1))
    }

    // 게시판 댓글 등록
    @PostMapping("/comment")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerClubBoardComment(@AuthUser     user        : User,
                                 @PathVariable clubSeq     : Long,
                                 @PathVariable clubBoardSeq     : Long,
                                 @RequestParam parentCommentSeq : Long?,
                                 @RequestBody  body: ClubBoardCommentRegisterDto): ResponseDto<String> {

        clubBoardCommentService.registerComment(clubSeq, clubBoardSeq, parentCommentSeq, user, body)
        return ResponseDto(data = "")
    }

    @PatchMapping("/{clubBoardCommentSeq}")
    fun editClubBoardComment(@AuthUser     user        : User,
                             @PathVariable clubSeq     : Long,
                             @PathVariable clubBoardSeq: Long,
                             @PathVariable clubBoardCommentSeq: Long,
                             @RequestBody body: ClubBoardCommentRegisterDto): ResponseDto<String> {

        clubBoardCommentService.editComment(clubSeq, clubBoardSeq, clubBoardCommentSeq, user, body)
        return ResponseDto(data = "")
    }

    @DeleteMapping("/{clubBoardCommentSeq}")
    fun deleteClubBoardComment(@AuthUser     user        : User,
                               @PathVariable clubSeq     : Long,
                               @PathVariable clubBoardSeq: Long,
                               @PathVariable clubBoardCommentSeq: Long): ResponseDto<String> {

        clubBoardCommentService.removeComment(clubSeq, clubBoardSeq, clubBoardCommentSeq, user)
        return ResponseDto("")
    }
}