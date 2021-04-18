package com.taskforce.superinvention.app.domain.club

import com.taskforce.superinvention.app.domain.club.album.ClubAlbumRepository
import com.taskforce.superinvention.app.domain.club.album.comment.ClubAlbumCommentRepository
import com.taskforce.superinvention.app.domain.club.album.like.ClubAlbumLikeRepository
import com.taskforce.superinvention.app.domain.club.board.ClubBoardRepository
import com.taskforce.superinvention.app.domain.club.board.comment.ClubBoardCommentRepository
import com.taskforce.superinvention.app.domain.club.board.img.ClubBoardImgRepository
import com.taskforce.superinvention.app.domain.club.board.like.ClubBoardLikeRepository
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.club.user.ClubUserService
import com.taskforce.superinvention.app.domain.common.image.webp.convert.WebpConvertService
import com.taskforce.superinvention.app.domain.interest.ClubInterest
import com.taskforce.superinvention.app.domain.interest.ClubInterestRepository
import com.taskforce.superinvention.app.domain.interest.interest.InterestService
import com.taskforce.superinvention.app.domain.meeting.MeetingApplicationRepository
import com.taskforce.superinvention.app.domain.meeting.MeetingRepository
import com.taskforce.superinvention.app.domain.meeting.MeetingService
import com.taskforce.superinvention.app.domain.role.ClubUserRole
import com.taskforce.superinvention.app.domain.role.ClubUserRoleRepository
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.region.ClubRegion
import com.taskforce.superinvention.app.domain.region.ClubRegionRepository
import com.taskforce.superinvention.app.domain.region.RegionService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.UserRepository
import com.taskforce.superinvention.app.web.dto.club.*
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumSearchOption
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardSearchOpt
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.interest.InterestWithPriorityDto
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto
import com.taskforce.superinvention.app.web.dto.region.RegionWithPriorityDto
import com.taskforce.superinvention.app.web.dto.role.RoleDto
import com.taskforce.superinvention.common.exception.BizException
import com.taskforce.superinvention.common.exception.club.CannotJoinClubException
import com.taskforce.superinvention.common.exception.club.ClubNotFoundException
import com.taskforce.superinvention.common.exception.club.UserIsNotClubMemberException
import com.taskforce.superinvention.common.util.aws.s3.AwsS3Mo
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import org.apache.commons.io.FilenameUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class ClubService(
    private var clubRepository: ClubRepository,
    private val clubUserService: ClubUserService,
    private var roleService: RoleService,
    private var interestService: InterestService,
    private var regionService: RegionService,
    private var clubUserRepository: ClubUserRepository,
    private var clubInterestRepository: ClubInterestRepository,
    private var clubRegionRepository: ClubRegionRepository,
    private var clubUserRoleRepository: ClubUserRoleRepository,
    private var clubAlbumRepository: ClubAlbumRepository,
    private val clubBoardRepository: ClubBoardRepository,
    private val clubBoardCommentRepository: ClubBoardCommentRepository,
    private val clubBoardLikeRepository: ClubBoardLikeRepository,
    private val clubBoardImgRepository: ClubBoardImgRepository,
    private val meetingRepository: MeetingRepository,
    private val meetingApplicationRepository: MeetingApplicationRepository,
    private val clubAlbumLikeRepository: ClubAlbumLikeRepository,
    private val webpConvertService: WebpConvertService,
    private val awsS3Mo: AwsS3Mo,
) {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(ClubService::class.java)
    }


    @Autowired
    lateinit var meetingService: MeetingService

    fun getValidClubBySeq(clubSeq: Long): Club {
        return clubRepository.findByIdOrNull(clubSeq)
            ?: throw ClubNotFoundException()
    }

    fun getClubUserDto(clubSeq: Long): ClubUsersDto? {
        val clubUsers = clubUserRepository.findByClubSeq(clubSeq)
        if (clubUsers.isEmpty()) throw BizException("모임에 유저가 한명도 존재하지 않습니다", HttpStatus.INTERNAL_SERVER_ERROR)
        return ClubUsersDto( clubUsers[0].club, clubUsers.map{ e -> e.user}.toList() )
    }

    // 모임 세부정보 조회
    @Transactional
    fun getClubInfoDetail(user: User?, clubSeq: Long): ClubInfoDetailsDto {

        // 모임 조회
        val clubInfo = clubRepository.findById(clubSeq)
                .orElseThrow { throw BizException("해당 클럽이 존재하지 않습니다", HttpStatus.NOT_FOUND) }

        // 모임 관심사 조회
        val clubInterest = clubInterestRepository.findWithInterestGroup(clubSeq)
                .map (::InterestWithPriorityDto)

        // 모임 지역 조회
        val clubRegions = clubRegionRepository.findByClubSeq(clubSeq)
                ?.map(::RegionWithPriorityDto) ?: emptyList()

        val clubInfoDto = ClubInfoDto(
                ClubDto(clubInfo),
                clubInterest,
                clubRegions
        )

        // [현재 조회한 인원이] 모임원일 경우 모임 권한, 좋아요 표시 여부 체크
        val clubUserDetails = clubUserService.getClubUserDetails(user, clubSeq)

        // 해당 모임에 가입된 유저들을 조회
        val clubMembers = clubUserRepository.findClubUsersInClub(clubSeq)
            .map(::ClubInfoUserDto)

        return ClubInfoDetailsDto(
                clubInfoDto,
                clubUserDetails,
                clubMembers
        )
    }

    /**
     * 새로운 모임을 생성한다.
     */
    @Transactional
    fun addClub(superUser: User, request: ClubAddRequestDto) {

        val club = Club(name = request.name,
            description = request.description,
            maximumNumber = request.maximumNumber,
            mainImageUrl  = null,
            mainImagePath = null,
            mainImageName = null,
        )

        // validation
        if (request.interestList.filter { e -> e.priority == 1L }.count() != 1)
            throw BizException("우선순위가 1인 관심사가 한개가 아닙니다", HttpStatus.BAD_REQUEST)

        if (request.regionList.filter { e -> e.priority == 1L }.count() != 1)
            throw BizException("우선순위가 1인 지역이 한개가 아닙니다", HttpStatus.BAD_REQUEST)

        // 1. 모임 생성
        val savedClub = clubRepository.save(club)

        // 2. 모임 사진 생성
        if(request.img != null) {
            val imgFolder = "club/${club.seq}"
            val movedFile: S3Path = awsS3Mo.moveFile(request.img!!, "$imgFolder/${request.img!!.fileName}")

            club.mainImageUrl  = movedFile.absolutePath
            club.mainImagePath = movedFile.filePath
            club.mainImageName = movedFile.fileName
        }

        // 3. 생성한 유저가 해당 모임에 들어감
        val superUserClub = ClubUser(savedClub, superUser, false)
        val savedClubUser = clubUserRepository.save(superUserClub)

        // 4. 해당 클럽에 관심사 부여
        val clubInterestList = request.interestList
            .map { e -> ClubInterest(savedClub, interestService.findBySeq(e.seq), e.priority) }
            .toList()
        clubInterestRepository.saveAll(clubInterestList)

        // 5. 해당 클럽에 지역 부여
        val clubRegionList = request.regionList.map { e -> ClubRegion(savedClub, regionService.findBySeq(e.seq), e.priority) }
        clubRegionRepository.saveAll(clubRegionList)

        // 6. 생성한 유저에게 모임장 권한을 부여
        val masterRole = roleService.findByRoleName(Role.RoleName.MASTER)
        val clubUserRole = ClubUserRole(savedClubUser, masterRole)
        clubUserRoleRepository.save(clubUserRole)
    }

    @Transactional
    fun getClubUserList(club: Club): List<ClubUser> {
        return clubUserRepository.findByClub(club)
    }

    @Transactional
    fun addClubUser(clubSeq: Long, user: User) {
        val club   = clubRepository.findBySeq(clubSeq)
        val clubUserList = getClubUserList(club)

        if (clubUserList.size >= club.maximumNumber) {
            throw IndexOutOfBoundsException("모임 최대 인원을 넘어, 회원가입이 불가합니다.")
        }
        

        // 모임 가입처리
        // 이전에 가입되어있었다면?
        val alreadyJoinedClubUser = club.clubUser.find { e -> e.user.seq == user.seq }
        if (alreadyJoinedClubUser != null) {
            // 강퇴된 유저는 재가입이 불가능하다
            val isKickedUser = alreadyJoinedClubUser.clubUserRoles
                .map { clubUserRole -> clubUserRole.role.name }
                .contains(Role.RoleName.NONE)
            // member만 재가입이 가능하다
            val isMember = alreadyJoinedClubUser.clubUserRoles
                .map { clubUserRole -> clubUserRole.role.name }
                .contains(Role.RoleName.MEMBER)

            if (isKickedUser) throw CannotJoinClubException("강퇴된 모임에는 다시 가입하실 수 없습니다.", HttpStatus.FORBIDDEN)
            if (!isMember) throw CannotJoinClubException("이미 가입한 모임입니다.", HttpStatus.CONFLICT)

            roleService.changeClubUserRoles(alreadyJoinedClubUser, setOf(roleService.findByRoleName(Role.RoleName.CLUB_MEMBER)))
        } else {
            val clubUser = clubUserRepository.save(ClubUser(club, user, false))
            // 디폴트로 모임원 권한 주기
            val memberRole = roleService.findByRoleName(Role.RoleName.CLUB_MEMBER)
            val clubUserRole = ClubUserRole(clubUser, memberRole)

            clubUserRoleRepository.save(clubUserRole)
        }
    }

    @Transactional(readOnly = true)
    fun search(request: ClubSearchRequestDto, pageable: Pageable): PageDto<ClubWithRegionInterestDto> {
        // 하위 지역까지 모두 입력하자

        val regionSeqList = arrayListOf<Long>()
        if (request.regionSeq != null){
            regionSeqList.add(request.regionSeq!!)
            val regions = regionService.findBySeq(request.regionSeq!!)
            regions.subRegions.forEach{e -> regionSeqList.add(e.seq!!)}
        }

        val result = clubRepository.search(request.text, regionSeqList, request.interestSeq, request.interestGroupSeq, pageable)
        val mappingContents = result.content.map { e ->  ClubWithRegionInterestDto(
                club = e,
                userCount = e.userCount ?: 0
        )}.toList()

        val resultPage = PageImpl(mappingContents, result.pageable, result.totalElements)
        return PageDto(resultPage)
    }

    @Transactional
    fun changeClubInterests(user: User, clubSeq: Long, interestDtos: Set<InterestRequestDto>): Club {
        val club = getValidClubBySeq(clubSeq)
        val clubUser: ClubUser = clubUserRepository.findByClubAndUser(club, user)
                ?: throw UserIsNotClubMemberException()

        if (!roleService.hasClubManagerAuth(clubUser)) throw BizException("권한이 없습니다", HttpStatus.FORBIDDEN)
        
        // 기존 관심사 삭제
        val toDelete: List<ClubInterest> = clubInterestRepository.findByClub(club)
        clubInterestRepository.deleteAll(toDelete)

        // 신규 관심사 등록
        val toAdd: List<ClubInterest> = interestDtos.map { interest -> ClubInterest(club, interestService.findBySeq(interest.seq) , interest.priority) }
        clubInterestRepository.saveAll(toAdd)

        return club
    }

    @Transactional
    fun changeClubRegions(user: User, clubSeq: Long, regionDtoList: Set<RegionRequestDto>) {
        val club = getValidClubBySeq(clubSeq)
        val clubUser: ClubUser = clubUserRepository.findByClubAndUser(club, user)
                ?: throw UserIsNotClubMemberException()

        if (!roleService.hasClubManagerAuth(clubUser)) throw BizException("권한이 없습니다", HttpStatus.FORBIDDEN)

        // 기존 모임 지역 삭제
        val toDelete: List<ClubRegion> = clubRegionRepository.findByClub(club)
        clubRegionRepository.deleteAll(toDelete)

        // 신규 모임 지역 등록
        val toAdd: List<ClubRegion> = regionDtoList.map { region -> ClubRegion(club, regionService.findBySeq(region.seq), region.priority) }
        clubRegionRepository.saveAll(toAdd)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun getClubWithPriorityDto(clubSeq: Long): ClubWithRegionInterestDto {
        val club = getValidClubBySeq(clubSeq)
        return ClubWithRegionInterestDto(club, club.clubUser.size.toLong())
    }

    @Transactional
    fun getClubUserInfo(clubSeq: Long, user: User): ClubUserDto {
        val clubUser: ClubUser = clubUserRepository.findByClubSeqAndUserSeq(clubSeq, user.seq!!)
                ?: throw BizException("모임원이 아닙니다. 접근 권한이 없습니다.", HttpStatus.FORBIDDEN)

        val clubUserRoles = roleService.getClubUserRoles(clubUser)
        return ClubUserDto(
                seq = clubUser.seq!!,
                userSeq = clubUser.user.seq!!,
                club = ClubDto(clubUser.club),
                roles = clubUserRoles.map { clubUserRole -> RoleDto(clubUserRole.role) }.toSet()
        )
    }

    @Transactional
    fun getClubUser(clubSeq: Long, user: User): ClubUser? {
        return clubUserRepository.findByClubSeqAndUserSeq(clubSeq, user.seq!!)
    }

    @Transactional
    fun getClubUserByClubUserSeq(clubUserSeq: Long): ClubUser? {
        return  clubUserRepository.findById(clubUserSeq).get()
    }

    @Transactional
    fun getUserClubList(user: User, pageable: Pageable): PageDto<ClubUserWithClubDetailsDto> {

        // 내 모임원 정보 조회
        val clubListInPage: Page<ClubUserDto> = clubRepository.findUserClubList(user, pageable)

        val clubSeqList = clubListInPage.toList().map { clubUser -> clubUser.club.seq!! }

        // 모임 관심사 조회
        val clubInterests = clubInterestRepository.findWithInterestGroupIn(clubSeqList)

        // 모임 지역 조회
        val clubRegions = clubRegionRepository.findByClubSeqIn(clubSeqList)

        val result: Page<ClubUserWithClubDetailsDto> = clubListInPage.map { clubUserDto ->
            ClubUserWithClubDetailsDto(
                    clubUserDto = clubUserDto,
                    interests = clubInterests.filter { it.club.seq == clubUserDto.club.seq }.map(::InterestWithPriorityDto),
                    regions = clubRegions.filter { it.club.seq == clubUserDto.club.seq }.map(::RegionWithPriorityDto)
            )
        }
        return PageDto(result)
    }

    @Transactional
    fun withdraw(clubUserSeq: Long, actorClubUser: Long) {
        val withdrawClubUser =
            clubUserRepository.findByIdOrNull(clubUserSeq) ?: throw BizException("$clubUserSeq 번 모임원은 존재하지 않습니다")
        val actorClubUser =
            clubUserRepository.findByIdOrNull(actorClubUser) ?: throw BizException("$actorClubUser 번 모임원은 존재하지 않습니다")

        // 이미 탈퇴한 유저는 아닌지 검사한다
        if (!roleService.hasClubMemberAuth(withdrawClubUser)) throw BizException("이미 탈퇴한 유저입니다")

        // 모임장은 탈퇴 불가. 모임장 탈퇴 전 모임장을 다른 모임원에게 넘겨야 한다
        if (roleService.hasClubMasterAuth(withdrawClubUser)) throw BizException("모임장은 탈퇴할 수 없습니다. 모임 삭제 또는 모임장을 변경해주세요")

        // 다른 모임원에 의한 강퇴일 때
        var isKicked = false
        if (withdrawClubUser != actorClubUser) {
            isKicked = true
            // 매니저 이상만 모임원에 대한 탈퇴처리가 가능하다
            if (!roleService.hasClubManagerAuth(actorClubUser)) throw BizException("모임원 강제 탈퇴 처리는 매니저 이상만 할 수 있습니다")

            // 매니저가 매니저 퇴출, 매니저가 모임장 퇴출은 불가능하다
            if (roleService.hasClubManagerAuth(withdrawClubUser) && !roleService.hasClubMasterAuth(actorClubUser))
                throw BizException("매니저 이상 권한자는 현재 권한으로 강제탈퇴 처리할 수 없습니다")
        }

        // 신청된 만남 모두 취소
        meetingService.cancelAllApplication(withdrawClubUser)
        
        // 모임원 권한 회수
        roleService.withdrawRole(withdrawClubUser, isKicked)
    }

    /**
     * 모임 삭제
     */
    @Transactional
    fun deleteClub(clubSeq: Long, actor: ClubUser) {
        if (!roleService.hasClubMasterAuth(actor)) {
            throw BizException("모임장만 모임을 삭제할 수 있습니다", HttpStatus.FORBIDDEN)
        }

        // 모임
        val club = clubRepository.findBySeq(clubSeq)
        LOG.info("모임 삭제 시작, 모임 시퀀스 : ${club.seq}, 모임명 : ${club.name}")

        // 모임 관심사
        val clubInterests = club.clubInterests
        LOG.info("삭제 대상 모임 관심사 ${clubInterests.size}개")

        // 모임 선호 지역
        val clubRegions = club.clubRegions
        LOG.info("삭제 대상 모임 지역 ${clubRegions.size}개")


        // 모임원
        val clubUserList = getClubUserList(club)
        LOG.info("삭제 대상 모임원 ${clubUserList.size}개")
        
        // 모임원 권한
        val clubUserRoleList = clubUserRoleRepository.findByClubUserIn(clubUserList)
        LOG.info("삭제 대상 모임원 권한 ${clubUserRoleList.size}개")

        // 모임 사진첩
        val clubAlbumList = clubAlbumRepository.findClubAlbumList(clubSeq, ClubAlbumSearchOption(), Pageable.unpaged()).toList()
        LOG.info("삭제 대상 사진첩 ${clubAlbumList.size}개")

        if (clubAlbumList.isNotEmpty()) {
            // 모임 사진첩 댓글
            val clubAlbumCommentList = clubAlbumList.flatMap { it.clubAlbumComments }.toList()
            LOG.info("삭제 대상 사진첩 댓글 ${clubAlbumCommentList.size}개")

            if (clubAlbumCommentList.isNotEmpty()) {
                LOG.info("사진첩 댓글 삭제 시작")
                for (clubAlbumComment in clubAlbumCommentList) {
                    clubAlbumComment.deleteFlag = true
                    clubAlbumComment.content    = "탈퇴한 모임원의 게시글입니다."
                }
            }

            // 모임 사진첩 좋아요
            val clubAlbumLikeList = clubAlbumLikeRepository.findByClubAlbumIn(clubAlbumList)
            if (clubAlbumLikeList.isNotEmpty()) {
                LOG.info("사진첩 좋아요 삭제 시작")
                clubAlbumLikeRepository.deleteAll(clubAlbumLikeList)
            }

            LOG.info("사진첩 삭제 시작")
            clubAlbumRepository.deleteAll(clubAlbumList)
        }

        // 모임 게시판
        val clubBoardList = clubBoardRepository.searchInList(Pageable.unpaged(), null, ClubBoardSearchOpt(), clubSeq).toList()
        LOG.info("삭제 대상 게시판 ${clubBoardList.size}개")

        if (clubBoardList.isNotEmpty()) {
            // 게시판 이미지
            val clubBoardImgList = clubBoardList.flatMap { it.boardImgs }.toList()
            LOG.info("삭제 대상 게시판 이미지 ${clubBoardImgList.size}개")

            // 게시판 좋아요
            val clubBoardLikeList = clubBoardLikeRepository.findByClubBoardIn(clubBoardList)
            LOG.info("삭제 대상 게시판 좋아요 ${clubBoardLikeList.size}개")

            // 게시판 댓글
            val clubBoardCommentList = clubBoardCommentRepository.findByClubBoardIn(clubBoardList)
            LOG.info("삭제 대상 게시판 댓글 ${clubBoardCommentList.size}개")

            if (clubBoardCommentList.isNotEmpty()) {
                LOG.info("게시판 댓글 삭제 시작")
                for (comment in clubBoardCommentList) {
                    comment.deleteFlag = true
                    comment.content    = "탈퇴된 사용자의 게시글입니다."
                }
            }

            if (clubBoardLikeList.isNotEmpty()) {
                LOG.info("게시판 좋아요 삭제 시작")
                clubBoardLikeRepository.deleteAll(clubBoardLikeList)
            }

            if (clubBoardImgList.isNotEmpty()) {
                LOG.info("게시판 이미지 삭제 시작")
                clubBoardImgRepository.deleteAll(clubBoardImgList)
            }

            LOG.info("게시판 삭제 시작")
            clubBoardRepository.deleteAll(clubBoardList)
        }

        // 만남
        val meetings = meetingRepository.getPagedMeetings(clubSeq, Pageable.unpaged()).toList()
        LOG.info("삭제 대상 만남 ${meetings.size}개")

        // 만남 신청
        val meetingApplications = meetingApplicationRepository.findByMeetingIn(meetings)
        LOG.info("삭제 대상 만남 신청 ${meetingApplications.size}개")

        if (meetings.isNotEmpty()){
            if (meetingApplications.isNotEmpty()) {
                LOG.info("만남 신청 삭제 시작")
                meetingApplicationRepository.deleteAll(meetingApplications)
            }

            LOG.info("만남 삭제 시작")
            meetingRepository.deleteAll(meetings)
        }

        if (clubUserRoleList.isNotEmpty()) {
            LOG.info("모임원 권한 삭제 시작")
            clubUserRoleRepository.deleteAll(clubUserRoleList)
        }

        if (clubUserList.isNotEmpty()) {
            LOG.info("모임원 삭제 시작")
            clubUserRepository.deleteAll(clubUserList)
        }

        if (clubInterests.isNotEmpty()) {
            LOG.info("모임 관심사 삭제 시작")
            clubInterestRepository.deleteAll(clubInterests)
        }

        if (clubRegions.isNotEmpty()) {
            LOG.info("모임 지역 삭제 시작")
            clubRegionRepository.deleteAll(clubRegions)
        }

        LOG.info("모임 삭제 시작")
        clubRepository.delete(club)
    }

    @Transactional
    fun modifyClub(clubSeq: Long, user: User, request: ClubAddRequestDto) {
        val club     = clubRepository.findBySeq(clubSeq)
        val clubUser = clubUserRepository.findByClubAndUser(club, user)
            ?: throw BizException("존재하지 않는 모임원의 요청입니다.", HttpStatus.FORBIDDEN)

        if (!roleService.hasClubManagerAuth(clubUser))
            throw BizException("모임의 매니저 이상만 모임 수정이 가능합니다.", HttpStatus.FORBIDDEN)

        changeClubInterests(user, clubSeq, request.interestList.toSet())
        changeClubRegions(user, clubSeq, request.regionList.toSet())

        club.apply {
            name = request.name
            description = request.description
            maximumNumber = request.maximumNumber
        }

        if(request.img != null) {
            val imgFolder = "club/${club.seq}"
            val movedFile: S3Path = awsS3Mo.moveFile(request.img!!, "$imgFolder/${request.img!!.fileName}")
            webpConvertService.convertToWebP(movedFile)

            if(club.mainImageUrl != null) {
                val webpFilePath  = "${FilenameUtils.removeExtension(club.mainImageUrl!!)}.webp"
                awsS3Mo.deleteFile(club.mainImageUrl!!)
                awsS3Mo.deleteFile(webpFilePath)
            }

            club.mainImageUrl  = request.img?.absolutePath
            club.mainImagePath = request.img?.filePath
            club.mainImageName = request.img?.fileName
        }
    }
}
