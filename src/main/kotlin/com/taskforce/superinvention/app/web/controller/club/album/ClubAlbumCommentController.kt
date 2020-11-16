package com.taskforce.superinvention.app.web.controller.club.album

import com.taskforce.superinvention.app.domain.club.album.comment.ClubAlbumCommentService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.club.album.comment.ClubAlbumCommentListDto
import com.taskforce.superinvention.app.web.dto.club.album.comment.ClubAlbumCommentRegisterDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/club/{clubSeq}/album/{clubAlbumSeq}")
class ClubAlbumCommentController(
        private val clubAlbumCommentService: ClubAlbumCommentService
) {

    // 클럽 댓글 조회
    @GetMapping("/comment")
    fun getClubAlbumComments(pageable: Pageable?,
                             @PathVariable clubSeq     : Long,
                             @PathVariable clubAlbumSeq: Long): ResponseDto<Page<ClubAlbumCommentListDto>> {
        return ResponseDto(clubAlbumCommentService.getCommentList(pageable, clubAlbumSeq))
    }

    // 클럽 댓글 등록
    @PostMapping("/comment")
    @ResponseStatus(HttpStatus.CREATED)
    fun registerClubAlbumComment(@AuthUser     user        : User,
                                 @PathVariable clubSeq     : Long,
                                 @PathVariable clubAlbumSeq: Long,
                                 @RequestBody  body: ClubAlbumCommentRegisterDto): ResponseDto<String> {

        clubAlbumCommentService.registerComment(clubSeq, clubAlbumSeq, user, body)
        return ResponseDto(data = "")
    }

    @PatchMapping("/{clubAlbumCommentSeq}")
    fun editClubAlbumComment(@AuthUser     user        : User,
                             @PathVariable clubSeq     : Long,
                             @PathVariable clubAlbumSeq: Long,
                             @PathVariable clubAlbumCommentSeq: Long,
                             @RequestBody body: ClubAlbumCommentRegisterDto): ResponseDto<String> {

        clubAlbumCommentService.editComment(clubSeq, clubAlbumSeq, clubAlbumCommentSeq, user, body)
        return ResponseDto(data = "")
    }

    @DeleteMapping("/{clubAlbumCommentSeq}")
    fun deleteClubAlbumComment(@AuthUser     user        : User,
                               @PathVariable clubSeq     : Long,
                               @PathVariable clubAlbumSeq: Long,
                               @PathVariable clubAlbumCommentSeq: Long): ResponseDto<String> {

        clubAlbumCommentService.removeComment(clubSeq, clubAlbumSeq, clubAlbumCommentSeq, user)
        return ResponseDto("")
    }
}