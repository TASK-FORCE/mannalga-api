package com.taskforce.superinvention.app.domain.club.album

import com.taskforce.superinvention.app.domain.club.ClubRepository
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumListDto
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumRegisterDto
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumSearchOption
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClubAlbumService(
        private val clubAlbumRepository: ClubAlbumRepository,
        private val clubRepository: ClubRepository
) {

    // 엘범 등록
    @Transactional
    fun registerClubAlbum(clubSeq: Long, clubAlbumDto: ClubAlbumRegisterDto) {
        val club = clubRepository.findBySeq(clubSeq)
        val clubAlbum = ClubAlbum(club, clubAlbumDto)
        clubAlbumRepository.save(clubAlbum)
    }

    @Transactional(readOnly = true)
    fun getClubAlbumList(clubSeq: Long, searchOption: ClubAlbumSearchOption, pageable: Pageable): Page<ClubAlbumListDto> {
        val query = clubAlbumRepository.findClubAlbumList(clubSeq, searchOption, pageable)

        val result = query.results.map { tuple ->
            val clubAlbum = tuple.get(1, ClubAlbum::class.java)
            ClubAlbumListDto(
                title     = clubAlbum?.title     ?: "",
                file_name = clubAlbum?.file_name ?: "",
                img_ur    = clubAlbum?.img_url   ?: "",
                likeCnt   = tuple.get(2, Long::class.java) ?: 0,
                commentCnt= tuple.get(3, Long::class.java) ?: 0
            )
        }
        return PageImpl(result, pageable, query.total)
    }

    @Transactional
    fun removeClubAlbum(clubAlbumSeq: Long) {
        val clubAlbum = clubAlbumRepository.findById(clubAlbumSeq)
        if(clubAlbum.isPresent) {
            clubAlbum.get().delete_flag = true
        }
    }
}