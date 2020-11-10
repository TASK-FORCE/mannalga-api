package com.taskforce.superinvention.app.web.controller.club.album

import com.taskforce.superinvention.app.domain.club.album.ClubAlbumService
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumListDto
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumRegisterDto
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumSearchOption
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
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
     @Secured(Role.CLUB_MEMBER)
     fun registerClubAlbum(@PathVariable clubSeq: Long,
                           @RequestBody body: ClubAlbumRegisterDto?): ResponseDto<String> {

          clubAlbumService.registerClubAlbum(clubSeq, body)
          return ResponseDto(data = "")
     }

     @DeleteMapping("/{clubAlbumSeq}")
     fun registerClubAlbum(@PathVariable clubAlbumSeq: Long) : ResponseDto<String> {
          clubAlbumService.removeClubAlbum(clubAlbumSeq)
          return ResponseDto(data = "")
     }
}