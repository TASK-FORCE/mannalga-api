package com.taskforce.superinvention.app.web.controller.club.album

import com.taskforce.superinvention.app.domain.club.album.ClubAlbumService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.club.album.*
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.common.config.argument.resolver.auth.AuthUser
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/club/{clubSeq}/album")
class ClubAlbumController(
        private val clubAlbumService: ClubAlbumService
) {

    // 모임 사진첩 목록 조회
    @GetMapping
    fun gerClubAlbumList(@PathVariable clubSeq:Long,
                        pageable: Pageable,
                        searchOption: ClubAlbumSearchOption): ResponseDto<PageDto<ClubAlbumListDto>> {

        return ResponseDto(clubAlbumService.getClubAlbumList(clubSeq, searchOption, pageable))
    }

    // 사진첩 개별 조회
    @GetMapping("/{clubAlbumSeq}")
    fun gerClubAlbum(@AuthUser     user: User?,
                     @PathVariable clubSeq      :Long,
                     @PathVariable clubAlbumSeq :Long): ResponseDto<ClubAlbumDto> {

        return ResponseDto(clubAlbumService.getClubAlbumDto(user, clubSeq, clubAlbumSeq))
    }

    // 모임 사진첩 등록
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun registerClubAlbum(@AuthUser     user: User,
                          @PathVariable clubSeq: Long,
                          @RequestBody  body: ClubAlbumRegisterDto): ResponseDto<String> {

        clubAlbumService.registerClubAlbum(user, clubSeq, body)
        return ResponseDto(data = "")
    }

    // 모임 사진첩 수정
    @PutMapping("/{clubAlbumSeq}")
    @ResponseStatus(HttpStatus.CREATED)
    fun editClubAlbum(@AuthUser     user         : User?,
                      @PathVariable clubSeq      : Long,
                      @PathVariable clubAlbumSeq :Long,
                      @RequestBody  body: ClubAlbumEditDto): ResponseDto<String> {

        clubAlbumService.editClubAlbum(user, clubSeq, clubAlbumSeq, body)
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
