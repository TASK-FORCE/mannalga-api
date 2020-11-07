package com.taskforce.superinvention.app.domain.club

import com.querydsl.core.QueryResults
import com.querydsl.core.Tuple
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
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
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto
import com.taskforce.superinvention.app.web.dto.role.RoleDto
import com.taskforce.superinvention.common.exception.BizException
import com.taskforce.superinvention.common.exception.club.UserIsNotClubMemberException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalArgumentException

@Service
class ClubService(
        private var clubRepository: ClubRepository,
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

    /**
     * 새로운 모임을 생성한다.
     */
    @Transactional
    fun addClub(club: Club, superUser: User, interestList: List<InterestRequestDto>, regionList: List<RegionRequestDto>) {
        // validation
        if (interestList.stream().filter { e -> e.priority == 1L }.count() != 1L)
            throw IllegalArgumentException("우선순위가 1인 관심사가 한개가 아닙니다")

        if (regionList.stream().filter { e -> e.priority == 1L }.count() != 1L)
            throw IllegalArgumentException("우선순위가 1인 지역이 한개가 아닙니다")

        // 1. 모임 생성
        val savedClub = clubRepository.save(club)

        // 2. 생성한 유저가 해당 모임에 들어감
        val superUserClub = ClubUser(savedClub, superUser)
        val savedClubUser = clubUserRepository.save(superUserClub)

        // 3. 해당 클럽에 관심사 부여
        val clubInterestList = interestList.map { e -> ClubInterest(savedClub, interestService.findBySeq(e.seq), e.priority) }.toList()
        clubInterestRepository.saveAll(clubInterestList)

        // 4. 해당 클럽에 지역 부여
        val clubRegionList = regionList.map { e -> ClubRegion(savedClub, regionService.findBySeq(e.seq), e.priority) }
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
        val clubUser = ClubUser(club, user)
        clubUserRepository.save(clubUser)

        // 디폴트로 모임원 권한 주기
        val memberRole = roleService.findByRoleName(Role.RoleName.CLUB_MEMBER)
        val clubUserRole = ClubUserRole(clubUser, memberRole)

        clubUserRoleRepository.save(clubUserRole)
    }

    @Transactional
    fun search(request: ClubSearchRequestDto, pageable: Pageable): Page<ClubWithRegionInterestDto> {
        val result = clubRepository.search(request.regionSeq, request.interestSeq, pageable)
        val mappingContents = result.content.map { e ->  ClubWithRegionInterestDto(
                club = e,
                userCount = e.clubUser.size.toLong()
        )}.toList()
        return PageImpl(mappingContents, result.pageable, result.totalElements)
    }

    @Transactional
    fun changeClubInterests(user: User, clubSeq: Long, interests: Set<InterestRequestDto>): Club {
        val club = getClubBySeq(clubSeq)
        val clubUser: ClubUser = clubUserRepository.findByClubAndUser(club, user)
                ?: throw UserIsNotClubMemberException()

        if (!roleService.hasClubManagerAuth(clubUser)) throw BizException("권한이 없습니다", HttpStatus.FORBIDDEN)
        
        // 기존 관심사 삭제
        val toDelete: List<ClubInterest> = clubInterestRepository.findByClub(club)
        clubInterestRepository.deleteAll(toDelete)

        // 신규 관심사 등록
        val toAdd: List<ClubInterest> = interests.map { interest -> ClubInterest(club, interestService.findBySeq(interest.seq) , interest.priority) }
        clubInterestRepository.saveAll(toAdd)

        return club
    }

    @Transactional
    fun changeClubRegions(user: User, clubSeq: Long, clubRegions: Set<RegionRequestDto>) {
        val club = getClubBySeq(clubSeq)
        val clubUser: ClubUser = clubUserRepository.findByClubAndUser(club, user)
                ?: throw UserIsNotClubMemberException()

        if (!roleService.hasClubManagerAuth(clubUser)) throw BizException("권한이 없습니다", HttpStatus.FORBIDDEN)

        // 기존 모임 지역 삭제
        val toDelete: List<ClubRegion> = clubRegionRepository.findByClub(club)
        clubRegionRepository.deleteAll(toDelete)

        // 신규 모임 지역 등록
        val toAdd: List<ClubRegion> = clubRegions.map { region -> ClubRegion(club, regionService.findBySeq(region.seq), region.priority) }
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
                club = ClubDto(clubUser.club, clubUser.club.clubUser.size.toLong()),
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
    fun getUserClubList(user: User, pageable: Pageable): Page<ClubUserDto> {
        val query: QueryResults<Tuple> = clubRepository.findUserClubList(user, pageable)

        val result: List<ClubUserDto> = query.results.map { tuple ->
            ClubUserDto(
                    seq     = tuple.get(0, Long::class.java)!!,
                    userSeq = tuple.get(1, Long::class.java)!!,
                    club    = ClubDto(
                            tuple.get(2, Club::class.java)!!,
                            tuple.get(3, Long::class.java)!!
                    ),
                    roles = toRoleSet(tuple.get(4, RoleDtoQueryProjection::class.java))
            )
        }

        return PageImpl(result, pageable, query.total)
    }

    private fun toRoleSet(concatedRole: RoleDtoQueryProjection?): Set<RoleDto> {
        if(concatedRole == null) return setOf()

        val roleNames= concatedRole.roleName.split(",")
        val roleGroupNames =  concatedRole.roleGroupName.split(",")

        val roleSet = mutableSetOf<RoleDto>()
        for(x in roleNames.indices) {
            roleSet.add(RoleDto("ROLE_${roleNames[x]}", roleGroupNames[x]))
        }
        return roleSet
    }
}