package com.taskforce.superinvention.app.web.controller.club

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.userInterest.UserInterestService
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.club.*
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto
import com.taskforce.superinvention.app.web.dto.role.RoleDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import com.taskforce.superinvention.common.exception.BizException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("clubs")
class ClubController(
        val clubService : ClubService,
        val userInterestService: UserInterestService,
        val roleService: RoleService
) {

    // 모임 상세 조회
    @GetMapping("/{seq}")
    fun getClubInfoDetail(@AuthUser user: User?, @PathVariable seq : Long): ResponseDto<ClubInfoDetailsDto> {
        val clubInfoDto = clubService.getClubInfoDetail(user, seq)
        return ResponseDto(data = clubInfoDto)
    }

    @GetMapping("/{seq}/users")
    fun getClubUser(@PathVariable seq : Long): ResponseDto<ClubUsersDto?> {
        val data = clubService.getClubUserDto(seq)
        return ResponseDto(data = data)
    }

    /**
     * 모임 가입
     */
    @PostMapping("/{clubSeq}/users")
    @ResponseStatus(HttpStatus.CREATED)
    fun addClubUser(@AuthUser user: User, @PathVariable clubSeq: Long): ResponseDto<Any?> {
        clubService.addClubUser(clubSeq, user)
        return ResponseDto(data = ResponseDto.EMPTY, message = "")
    }

    /**
     * 모임 생성
     * @author eric
     */
    @Secured(Role.MEMBER)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addClub(@AuthUser user: User, @RequestBody request: ClubAddRequestDto): ResponseDto<Any?> {
        val club = Club(name = request.name, description = request.description, maximumNumber = request.maximumNumber, mainImageUrl = request.mainImageUrl)
        clubService.addClub(club, user, request.interestList, request.regionList)

        return ResponseDto(data = ResponseDto.EMPTY, message = "")
    }

    /**
     * 모임리스트 검색
     * 검색에 관해 논의한 내용 [https://github.com/TASK-FORCE/super-invention/issues/109]
     * @author eric
     */
    @GetMapping("/search")
    fun getClubList(@AuthUser user: User, request: ClubSearchRequestDto, pageable: Pageable): ResponseDto<Page<ClubWithRegionInterestDto>> {
        // validation
        if (request.interestSeq != null && request.interestGroupSeq != null)
            throw BizException("관심사 그룹과 관심사 중 하나만 선택해서 검색할 수 있습니다.", HttpStatus.BAD_REQUEST)

        val data = clubService.search(request, pageable)
        return ResponseDto(data = data)
    }

    /**
     * 모임 관심사 변경
     * @author eric
     */
    @PutMapping("/{clubSeq}/interests")
    @Secured(Role.MEMBER)
    fun changeClubInterest(@AuthUser user: User, @PathVariable clubSeq: Long, @RequestBody clubInterests: Set<InterestRequestDto>): ResponseDto<ClubWithRegionInterestDto> {
        clubService.changeClubInterests(user, clubSeq, clubInterests)
        val data = clubService.getClubWithPriorityDto(clubSeq)
        return ResponseDto(data = data)
    }

    /**
     * 모임 지역 변경
     * @author eric
     */
    @PutMapping("{clubSeq}/regions")
    @Secured(Role.MEMBER)
    fun changeClubRegion(@AuthUser user: User, @PathVariable clubSeq: Long, @RequestBody clubRegions: Set<RegionRequestDto>): ResponseDto<ClubWithRegionInterestDto> {
        clubService.changeClubRegions(user, clubSeq, clubRegions)
        val data = clubService.getClubWithPriorityDto(clubSeq)
        return ResponseDto(data = data)
    }

    /**
     * 모임 내부 내 정보 조회
     */
    @GetMapping("/{clubSeq}/my-info")
    @Secured(Role.MEMBER)
    fun getCurrentClubUserInfo(@AuthUser user: User, @PathVariable("clubSeq") clubSeq: Long): ResponseDto<ClubUserDto> {
        val clubUserInfo = clubService.getClubUserInfo(clubSeq, user)
        return ResponseDto(data = clubUserInfo)
    }

    /**
     * 권한 부여 및 회수는 모임장만 할 수 있도록 만든다.
     */
    @PutMapping("/{clubSeq}/users/{clubUserSeq}/roles")
    @Secured(Role.MEMBER)
    fun changeClubUserRole(@AuthUser user: User,
                           @PathVariable clubSeq: Long,
                           @PathVariable clubUserSeq: Long,
                           @RequestBody roleSeqList: Set<Long>): ResponseDto<Set<RoleDto>> {
        // 현재 유저가 모임에 가입은 했는지
        val currentClubUser = clubService.getClubUser(clubSeq, user) ?: throw BizException("권한이 없습니다.", HttpStatus.FORBIDDEN)

        // 모임장인지
        val hasClubManagerAuth = roleService.hasClubMasterAuth(currentClubUser)

        if (!hasClubManagerAuth) throw BizException("모임원 권한 변경은 모임장만 가능합니다.", HttpStatus.FORBIDDEN)

        // 타겟으로 잡은 대상이 현재 모임원인지
        val targetClubUser: ClubUser = clubService.getClubUserByClubUserSeq(clubUserSeq)
                ?: throw BizException("존재하지 않는 모임원입니다.", HttpStatus.NOT_FOUND)
        if (targetClubUser.club.seq != clubSeq) throw BizException("조회한 모임의 모임원이 아닙니다", HttpStatus.FORBIDDEN)

        // 타겟으로 잡은 대상이 모임장이라면, 예외처리하자 (모임장은 고정되어있어야함, 양도만 가능)
        if (roleService.hasClubMasterAuth(targetClubUser)) {
            throw BizException("모임장의 권한을 변경할 수 없습니다", HttpStatus.CONFLICT)
        }

        val roles: Set<Role> =  roleService.findBySeqList(roleSeqList)
        roleService.changeClubUserRoles(targetClubUser, roles)

        return ResponseDto(data = roles.map { role -> RoleDto(role) }.toSet())
    }

    @GetMapping("/my")
    @Secured(Role.MEMBER)
    fun getMyClubList(@AuthUser user: User): ResponseDto<List<ClubUserWithClubDetailsDto>> {
        return ResponseDto(data = clubService.getUserClubList(user))
    }
}