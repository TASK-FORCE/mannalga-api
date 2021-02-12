package com.taskforce.superinvention.app.domain.club.album

import com.taskforce.superinvention.app.domain.club.ClubRepository
import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.club.album.image.ClubAlbumImageService
import com.taskforce.superinvention.app.domain.club.album.like.ClubAlbumLikeRepository
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.club.user.ClubUserService
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumDto
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumListDto
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumRegisterDto
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumSearchOption
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.common.exception.BizException
import com.taskforce.superinvention.common.exception.club.ClubNotFoundException
import com.taskforce.superinvention.common.exception.club.UserIsNotClubMemberException
import com.taskforce.superinvention.common.exception.club.album.ClubAlbumNotFoundException
import com.taskforce.superinvention.common.exception.club.album.NoAuthForClubAlbumException
import com.taskforce.superinvention.common.exception.common.IsAlreadyDeletedException
import com.taskforce.superinvention.common.util.aws.s3.AwsS3Mo
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClubAlbumService(
        private val roleService: RoleService,
        private val clubService: ClubService,
        private val clubUserService: ClubUserService,
        private val clubAlbumImgService: ClubAlbumImageService,
        private val clubUserRepository : ClubUserRepository,
        private val clubAlbumRepository: ClubAlbumRepository,
        private val clubRepository: ClubRepository,
        private val clubAlbumLikeRepository: ClubAlbumLikeRepository,
) {

    companion object {
        private val clubNotFoundException        = ClubNotFoundException()
        private val userIsNotClubMemberException = UserIsNotClubMemberException()
        private val clubAlbumNotFoundException   = ClubAlbumNotFoundException()
        private val isAlreadyDeletedException    = IsAlreadyDeletedException()
        private val noAuthForClubAlbumException  = NoAuthForClubAlbumException()
    }

    fun getValidClubAlbumBySeq(clubAlbumSeq: Long?): ClubAlbum {
        return clubAlbumRepository.findByIdOrNull(clubAlbumSeq)
            ?: throw ClubAlbumNotFoundException()
    }

    // 엘범 등록
    @Transactional
    fun registerClubAlbum(user: User?, clubSeq: Long, clubAlbumDto: ClubAlbumRegisterDto?): Boolean {
        user ?: throw UserIsNotClubMemberException()

        val club     = clubService.getValidClubBySeq(clubSeq)
        val clubUser = clubUserService.getValidClubUser(clubSeq, user)
        if(isValid(clubAlbumDto!!)) {
            val clubAlbum = ClubAlbum (
                writer      = clubUser,
                club        = club,
                registerDto = clubAlbumDto
            )
            clubAlbumRepository.save(clubAlbum)
            clubAlbumImgService.registerClubAlbumImage(clubAlbum, clubAlbumDto.image)
            return true
        }
        return false
    }

    @Transactional(readOnly = true)
    fun getClubAlbumList(clubSeq: Long, searchOption: ClubAlbumSearchOption, pageable: Pageable): PageDto<ClubAlbumListDto> {
        val result: Page<ClubAlbumListDto> = clubAlbumRepository.findClubAlbumList(clubSeq, searchOption, pageable)
            .map(::ClubAlbumListDto)

        return PageDto(result)
    }

    @Transactional(readOnly = true)
    fun getClubAlbumDto(user: User?, clubSeq: Long, clubAlbumSeq: Long?): ClubAlbumDto {

        // 조회자가 좋아요를 눌렀을 경우
        val isLiked = user
            ?.let { clubUserRepository.findByClubSeqAndUser(clubSeq, user) }
            ?.let { clubUser ->  clubAlbumLikeRepository.findByClubAlbumSeqAndClubUser(clubAlbumSeq!!, clubUser) }
            ?.let { true } ?: false

        return ClubAlbumDto(getValidClubAlbumBySeq(clubAlbumSeq), isLiked)
    }

    @Transactional
    fun removeClubAlbum(user: User, clubSeq: Long, clubAlbumSeq: Long) {

        val club = clubRepository.findByIdOrNull(clubSeq)
            ?: throw clubNotFoundException

        val clubUser = clubUserRepository.findByClubAndUser(club, user)
            ?: throw userIsNotClubMemberException

        val clubAlbum: ClubAlbum = clubAlbumRepository.findByIdOrNull(clubAlbumSeq)
            ?: throw clubAlbumNotFoundException

        if(clubAlbum.delete_flag) {
            throw isAlreadyDeletedException
        }

        if(eqSeq(clubAlbum.writer, clubUser)) {
            clubAlbum.delete_flag = true
            return
        }

        if(roleService.hasClubManagerAuth(clubUser)) {
            clubAlbum.delete_flag = true
            return
        }

        throw noAuthForClubAlbumException
    }

    private fun isValid(clubAlbumDto: ClubAlbumRegisterDto): Boolean {

        if(clubAlbumDto.image.absolutePath.isBlank()) {
            throw BizException("잘못된 이미지 URL입니다.", HttpStatus.BAD_REQUEST)
        }

        if(clubAlbumDto.title.isBlank()) {
            throw BizException("제목이 비어있습니다.", HttpStatus.BAD_REQUEST)
        }
        return true
    }

    private fun eqSeq(writer: ClubUser, clubUser: ClubUser): Boolean {
        return writer.seq == clubUser.seq
    }
}