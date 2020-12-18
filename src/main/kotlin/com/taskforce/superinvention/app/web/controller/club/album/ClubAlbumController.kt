package com.taskforce.superinvention.app.web.controller.club.album

import com.taskforce.superinvention.app.domain.club.album.ClubAlbumService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumListDto
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumRegisterDto
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumSearchOption
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/club/{clubSeq}/album")
class ClubAlbumController(
        private val clubAlbumService: ClubAlbumService
) {

     @GetMapping
     fun gerClubAlbumList(pageable: Pageable?,
                          @PathVariable clubSeq:Long,
                          searchOption: ClubAlbumSearchOption): ResponseDto<Page<ClubAlbumListDto>> {

          return ResponseDto(clubAlbumService.getClubAlbumList(clubSeq, searchOption, pageable))
     }

     @PostMapping
     @ResponseStatus(HttpStatus.CREATED)
     fun registerClubAlbum(@AuthUser     user: User,
                           @PathVariable clubSeq: Long,
                           @RequestBody  body: ClubAlbumRegisterDto): ResponseDto<String> {

          clubAlbumService.registerClubAlbum(user, clubSeq, body)
          return ResponseDto(data = "")
     }

     @DeleteMapping("/{clubAlbumSeq}")
     fun deleteClubAlbum(@AuthUser     user: User,
                         @PathVariable clubSeq:Long,
                         @PathVariable clubAlbumSeq: Long) : ResponseDto<String> {

          clubAlbumService.removeClubAlbum(user, clubSeq, clubAlbumSeq)
          return ResponseDto(data = "")
     }
}