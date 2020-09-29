package com.taskforce.superinvention.app.domain.club

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
import com.taskforce.superinvention.app.web.dto.club.ClubSearchRequestDto
import com.taskforce.superinvention.app.web.dto.club.ClubUserDto
import com.taskforce.superinvention.app.web.dto.club.ClubWithRegionInterestDto
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.lang.IllegalArgumentException
import java.lang.RuntimeException

@Service
class ClubService(
        private var clubRepository: ClubRepository,
        private var clubRepositorySupport: ClubRepositorySupport,
        private var clubUserRepository: ClubUserRepository,
        private var clubUserRepositorySupport: ClubUserRepositorySupport,
        private var roleService: RoleService,
        private var interestService: InterestService,
        private var regionService: RegionService,
        private var clubInterestRepository: ClubInterestRepository,
        private var clubRegionRepository: ClubRegionRepository,
        private var clubUserRoleRepository: ClubUserRoleRepository
) {
    fun getClubBySeq(seq: Long): Club {
        val club = clubRepository.findById(seq).orElseThrow { NullPointerException() }
        return club
    }

    fun getClubUserDto(clubSeq: Long): ClubUserDto? {
        val clubUsers = clubUserRepositorySupport.findByClubSeq(clubSeq)
        return ClubUserDto( clubUsers[0].club, clubUsers.map{ e -> e.user}.toList() )
    }

    /**
     * 새로운 모임을 생성한다.
     */
    @Transactional
    fun addClub(club:Club, superUser: User, interestList: List<InterestRequestDto>, regionList: List<RegionRequestDto>) {
        // validation
        if (interestList.stream().filter({e -> e.priority.equals(1L)}).count() != 1L)
            throw IllegalArgumentException("우선순위가 1인 관심사가 한개가 아닙니다")

        if (regionList.stream().filter({ e -> e.priority.equals(1L)}).count() != 1L)
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
        return clubUserRepository.findByClub(club);
    }

    @Transactional
    fun addClubUser(club: Club, user: User) {
        val clubUserList = getClubUserList(club)
        if (clubUserList.size >= club.maximumNumber) {
            throw IndexOutOfBoundsException("모임 최대 인원을 넘어, 회원가입이 불가합니다.")
        }
        if (clubUserList.map { cu -> cu.user }.contains(user)) {
            throw RuntimeException("이미 가입한 모임입니다.")
        }
        val clubUser = ClubUser(club = club, user = user)
        clubUserRepository.save(clubUser)
    }


    @Transactional
    fun search(request: ClubSearchRequestDto): Page<ClubWithRegionInterestDto> {
        val pageable:Pageable = PageRequest.of(request.offset.toInt(), request.size.toInt())
        val result = clubRepositorySupport.search(request.searchOptions, pageable)
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
        if (!roleService.hasClubManagerAuth(clubUser)) throw RuntimeException("권한이 없습니다")
        
        // 기존 관심사 삭제
        val toDelete: List<ClubInterest> = clubInterestRepository.findByClub(club)
        clubInterestRepository.deleteAll(toDelete)

        // 신규 관심사 등록
        val toAdd: List<ClubInterest> = interests.map { interest -> ClubInterest(club, interestService.findBySeq(interest.seq) , interest.priority) }
        clubInterestRepository.saveAll(toAdd)

        return club
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    fun getClubWithPriorityDto(clubSeq: Long): ClubWithRegionInterestDto {
        val club = getClubBySeq(clubSeq)
        return ClubWithRegionInterestDto(club, club.clubUser.size.toLong())
    }
}