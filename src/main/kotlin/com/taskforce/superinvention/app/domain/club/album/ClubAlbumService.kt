package com.taskforce.superinvention.app.domain.club.album

import com.taskforce.superinvention.common.util.aws.s3.S3Path
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClubAlbumService(
        private val clubAlbumRepository: ClubAlbumRepository
) {

    // 엘범 등록
    @Transactional
    fun registerClubAlbum(s3Path: S3Path) {
        // ClubAlbum

        // clubAlbumRepository.save()
    }

    @Transactional(readOnly = true)
    fun getClubAlbumList(clubSeq: Long) {

    }
}