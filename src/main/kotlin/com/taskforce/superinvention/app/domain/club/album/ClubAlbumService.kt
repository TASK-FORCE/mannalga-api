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
import com.taskforce.superinvention.app.web.dto.club.album.*
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.common.exception.BizException
import com.taskforce.superinvention.common.exception.InvalidInputException
import com.taskforce.superinvention.common.exception.auth.InsufficientAuthException
import com.taskforce.superinvention.common.exception.auth.WithdrawClubUserNotAllowedException
import com.taskforce.superinvention.common.exception.club.ClubNotFoundException
import com.taskforce.superinvention.common.exception.club.UserIsNotClubMemberException
import com.taskforce.superinvention.common.exception.club.album.ClubAlbumNotFoundException
import com.taskforce.superinvention.common.exception.club.album.NoAuthForClubAlbumException
import com.taskforce.superinvention.common.exception.common.IsAlreadyDeletedException
import com.taskforce.superinvention.common.util.aws.s3.isValidPath
import org.springframework.beans.factory.annotation.Value
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
    private val clubAlbumLikeRepository: ClubAlbumLikeRepository,

    @Value("\${host.static.path}")
    private var imgHost: String,
) {

    companion object {
        private val userIsNotClubMemberException = UserIsNotClubMemberException()
        private val clubAlbumNotFoundException   = ClubAlbumNotFoundException()
        private val isAlreadyDeletedException    = IsAlreadyDeletedException()
        private val noAuthForClubAlbumException  = NoAuthForClubAlbumException()
    }

    fun getValidClubAlbumBySeq(clubAlbumSeq: Long?): ClubAlbum {
        return clubAlbumRepository.findByIdOrNull(clubAlbumSeq)
            ?: throw clubAlbumNotFoundException
    }

    fun getValidClubAlbumWithWriterBySeq(clubAlbumSeq: Long): ClubAlbum {
        return clubAlbumRepository.findBySeqWithWriter(clubAlbumSeq)
            ?: throw clubAlbumNotFoundException
    }

    @Transactional
    fun registerClubAlbum(user: User?, clubSeq: Long, clubAlbumDto: ClubAlbumRegisterDto): ClubAlbum {
        user ?: throw userIsNotClubMemberException

        val club     = clubService.getValidClubBySeq(clubSeq)
        val clubUser = clubUserService.getValidClubUser(clubSeq, user)

        if(!roleService.hasClubMemberAuth(clubUser)) {
            throw WithdrawClubUserNotAllowedException()
        }

        if(!isValid(clubAlbumDto)) {
            throw InvalidInputException()
        }

        val clubAlbum = clubAlbumRepository.save(
            ClubAlbum (
                writer      = clubUser,
                club        = club,
                registerDto = clubAlbumDto
            )
        )

        clubAlbumImgService.registerClubAlbumImage(clubAlbum, clubAlbumDto.image)

        return clubAlbum
    }

    // 사진첩 수정
    @Transactional
    fun editClubAlbum(user: User?, clubSeq: Long, clubAlbumSeq: Long, body: ClubAlbumEditDto): ClubAlbum {
        user ?: throw UserIsNotClubMemberException()

        val club      = clubService.getValidClubBySeq(clubSeq)
        val clubUser  = clubUserService.getValidClubUser(clubSeq, user)
        val clubAlbum = getValidClubAlbumWithWriterBySeq(clubAlbumSeq)

        if(clubAlbum.writer != clubUser) {
            throw InsufficientAuthException("작성자 이외에는 글을 수정할 수 없습니다.")
        }

        if(!roleService.hasClubMemberAuth(clubUser)) {
            throw WithdrawClubUserNotAllowedException("탈퇴한 상태에서 글을 수정할 수 없습니다.")
        }

        if(!body.title.isNullOrBlank()) {
            clubAlbum.title = body.title
        }

        if(body.image != null && body.image.isValidPath()) {
            clubAlbumImgService.editClubAlbumImage(clubAlbum, body.image)
        }

        return clubAlbum
    }

    @Transactional(readOnly = true)
    fun getClubAlbumList(clubSeq: Long, searchOption: ClubAlbumSearchOption, pageable: Pageable): PageDto<ClubAlbumListDto> {
        val result = clubAlbumRepository.findClubAlbumList(clubSeq, searchOption, pageable)
            .map{clubAlbum -> ClubAlbumListDto(imgHost, clubAlbum) }

        return PageDto(result)
    }

    @Transactional(readOnly = true)
    fun getClubAlbumDto(user: User?, clubSeq: Long, clubAlbumSeq: Long?): ClubAlbumDto {

        // 조회자가 좋아요를 눌렀을 경우
        val isLiked = user
            ?.let { clubUserRepository.findByClubSeqAndUser(clubSeq, user) }
            ?.let { clubUser ->  clubAlbumLikeRepository.findByClubAlbumSeqAndClubUser(clubAlbumSeq!!, clubUser) }
            ?.let { true } ?: false

        return ClubAlbumDto(imgHost, getValidClubAlbumBySeq(clubAlbumSeq), isLiked)
    }

    @Transactional
    fun removeClubAlbum(user: User, clubSeq: Long, clubAlbumSeq: Long) {

        val club      = clubService.getValidClubBySeq(clubSeq)
        val clubUser  = clubUserService.getValidClubUser(club.seq!!, user)
        val clubAlbum = getValidClubAlbumBySeq(clubAlbumSeq)

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
