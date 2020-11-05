package com.taskforce.superinvention.app.web.controller.club.album

import com.taskforce.superinvention.app.domain.club.album.like.ClubAlbumLikeService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("club/{clubSeq}/album/{clubAlbumSeq}/like")
class ClubAlbumLikeController(
        private val clubAlbumLikeService: ClubAlbumLikeService
) {
    @PostMapping
    fun registerClubAlbumLike(@AuthUser user: User,
                              @PathVariable clubSeq: Long,
                              @PathVariable clubAlbumSeq: Long): ResponseDto<String> {
        clubAlbumLikeService.registerClubAlbumLike(user, clubSeq, clubAlbumSeq)
        return ResponseDto("")
    }

    @DeleteMapping
    fun removeClubAlbumLike(@AuthUser user: User,
                            @PathVariable clubSeq: Long,
                            @PathVariable clubAlbumSeq: Long): ResponseDto<String> {
        clubAlbumLikeService.removeClubAlbumLike(user, clubSeq, clubAlbumSeq)
        return ResponseDto("")
    }
}