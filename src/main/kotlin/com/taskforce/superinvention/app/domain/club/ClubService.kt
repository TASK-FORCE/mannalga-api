package com.taskforce.superinvention.app.domain.club

import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.club.user.ClubUserService
import com.taskforce.superinvention.app.domain.interest.ClubInterest
import com.taskforce.superinvention.app.domain.interest.ClubInterestRepository
import com.taskforce.superinvention.app.domain.interest.interest.InterestService
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
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.interest.InterestWithPriorityDto
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto
import com.taskforce.superinvention.app.web.dto.region.SimpleRegionDto
import com.taskforce.superinvention.app.web.dto.role.RoleDto
import com.taskforce.superinvention.common.exception.BizException
import com.taskforce.superinvention.common.exception.club.UserIsNotClubMemberException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class ClubService(
        private var clubRepository: ClubRepository,
        private val clubUserService: ClubUserService,
        private var roleService: RoleService,
        private var interestService: InterestService,
        private var regionService: RegionService,
        private var userRepository: UserRepository,
        private var clubUserRepository: ClubUserRepository,
        private var clubInterestRepository: ClubInterestRepository,
        private var clubRegionRepository: ClubRegionRepository,
        private var clubUserRoleRepository: ClubUserRoleRepository
) {
    fun getClubBySeq(seq: Long): Club {
        val club = clubRepository.findById(seq).orElseThrow { NullPointerException() }
        return club
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
                ?.map(::SimpleRegionDto) ?: emptyList()

        val clubInfoDto = ClubInfoDto(
                ClubDto(clubInfo),
                clubInterest,
                clubRegions
        )

        // 모임원일 경우 모임 권한, 좋아요 표시 여부 체크
        val clubUserDetails = clubUserService.getClubUserDetails(user, clubSeq)

        return ClubInfoDetailsDto(
                clubInfoDto,
                clubUserDetails
        )
    }

    /**
     * 새로운 모임을 생성한다.
     */
    @Transactional
    fun addClub(club: Club, superUser: User, interestDtoList: List<InterestRequestDto>, regionDtoList: List<RegionRequestDto>) {
        // validation
        if (interestDtoList.stream().filter { e -> e.priority == 1L }.count() != 1L)
            throw BizException("우선순위가 1인 관심사가 한개가 아닙니다", HttpStatus.BAD_REQUEST)

        if (regionDtoList.stream().filter { e -> e.priority == 1L }.count() != 1L)
            throw BizException("우선순위가 1인 지역이 한개가 아닙니다", HttpStatus.BAD_REQUEST)

        // 1. 모임 생성
        val savedClub = clubRepository.save(club)

        // 2. 생성한 유저가 해당 모임에 들어감
        val superUserClub = ClubUser(savedClub, superUser, false)
        val savedClubUser = clubUserRepository.save(superUserClub)

        // 3. 해당 클럽에 관심사 부여
        interestService.checkBeforeConvertClubInterest(interestService.findBySeqList(interestDtoList.map { it.seq }))

        val clubInterestList = interestDtoList.map { e -> ClubInterest(savedClub, interestService.findBySeq(e.seq), e.priority) }.toList()
        clubInterestRepository.saveAll(clubInterestList)

        // 4. 해당 클럽에 지역 부여
        regionService.checkBeforeConvertClubRegion(regionService.findBySeqList(regionDtoList.map { it.seq }))
        val clubRegionList = regionDtoList.map { e -> ClubRegion(savedClub, regionService.findBySeq(e.seq), e.priority) }
        clubRegionRepository.saveAll(clubRegionList)

        // 5. 생성한 유저에게 모임장 권한을 부여
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

        if (clubUserList.map { cu -> cu.user.seq }.contains(user.seq)) {
            throw BizException("이미 가입한 모임입니다.", HttpStatus.CONFLICT)
        }

        // 모임 가입처리
        val clubUser = ClubUser(club, user, false)
        clubUserRepository.save(clubUser)

        // 디폴트로 모임원 권한 주기
        val memberRole = roleService.findByRoleName(Role.RoleName.CLUB_MEMBER)
        val clubUserRole = ClubUserRole(clubUser, memberRole)

        clubUserRoleRepository.save(clubUserRole)
    }

    @Transactional(readOnly = true)
    fun search(request: ClubSearchRequestDto, pageable: Pageable): Page<ClubWithRegionInterestDto> {
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
                userCount = e.clubUser.size.toLong()
        )}.toList()
        return PageImpl(mappingContents, result.pageable, result.totalElements)
    }

    @Transactional
    fun changeClubInterests(user: User, clubSeq: Long, interestDtos: Set<InterestRequestDto>): Club {
        val club = getClubBySeq(clubSeq)
        val clubUser: ClubUser = clubUserRepository.findByClubAndUser(club, user)
                ?: throw UserIsNotClubMemberException()

        if (!roleService.hasClubManagerAuth(clubUser)) throw BizException("권한이 없습니다", HttpStatus.FORBIDDEN)
        
        // 기존 관심사 삭제
        val toDelete: List<ClubInterest> = clubInterestRepository.findByClub(club)
        clubInterestRepository.deleteAll(toDelete)

        // 신규 관심사 등록
        interestService.checkBeforeConvertClubInterest(interestService.findBySeqList(interestDtos.map { it.seq }))

        val toAdd: List<ClubInterest> = interestDtos.map { interest -> ClubInterest(club, interestService.findBySeq(interest.seq) , interest.priority) }
        clubInterestRepository.saveAll(toAdd)

        return club
    }

    @Transactional
    fun changeClubRegions(user: User, clubSeq: Long, regionDtoList: Set<RegionRequestDto>) {
        val club = getClubBySeq(clubSeq)
        val clubUser: ClubUser = clubUserRepository.findByClubAndUser(club, user)
                ?: throw UserIsNotClubMemberException()

        if (!roleService.hasClubManagerAuth(clubUser)) throw BizException("권한이 없습니다", HttpStatus.FORBIDDEN)

        // 기존 모임 지역 삭제
        val toDelete: List<ClubRegion> = clubRegionRepository.findByClub(club)
        clubRegionRepository.deleteAll(toDelete)

        // 신규 모임 지역 등록
        regionService.checkBeforeConvertClubRegion(regionService.findBySeqList(regionDtoList.map { it.seq }))
        val toAdd: List<ClubRegion> = regionDtoList.map { region -> ClubRegion(club, regionService.findBySeq(region.seq), region.priority) }
        clubRegionRepository.saveAll(toAdd)
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun getClubWithPriorityDto(clubSeq: Long): ClubWithRegionInterestDto {
        val club = getClubBySeq(clubSeq)
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
    fun getUserClubList(user: User, pageable: Pageable): Page<ClubUserWithClubDetailsDto> {

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
                    regions = clubRegions.filter { it.club.seq == clubUserDto.club.seq }.map(::SimpleRegionDto)
            )
        }
        return result
    }
}