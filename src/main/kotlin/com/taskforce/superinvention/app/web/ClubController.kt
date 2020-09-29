package com.taskforce.superinvention.app.web

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.club.ClubUser
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.userInterest.UserInterestService
import com.taskforce.superinvention.app.domain.user.userRegion.UserRegionService
import com.taskforce.superinvention.app.web.common.request.PageOption
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.club.*
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.region.RegionRequestDto
import com.taskforce.superinvention.app.web.dto.role.RoleDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import com.taskforce.superinvention.common.exception.BizException
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("clubs")
class ClubController(
        val clubService : ClubService,
        val userRegionService: UserRegionService,
        val userInterestService: UserInterestService,
        val roleService: RoleService
) {
    @GetMapping("/{seq}")
    fun getClubBySeq(@PathVariable seq : Long): ResponseDto<Club?> {
        val data = clubService.getClubBySeq(seq)
        return ResponseDto(data = data)
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
    fun addClubUser(@AuthUser user: User, @PathVariable("clubSeq") clubSeq: Long): ResponseDto<Any?> {
        val club = clubService.getClubBySeq(clubSeq)
        clubService.addClubUser(club, user)
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
     * @author eric
     */
    @PostMapping("/search")
    fun getClubList(@AuthUser user: User, @RequestBody request: ClubSearchRequestDto): ResponseDto<Page<ClubWithRegionInterestDto>> {
        if (ObjectUtils.isEmpty(request.searchOptions.regionList)) {
            val userRegionDto = userRegionService.findUserRegionList(user)
            request.searchOptions.regionList = userRegionDto.userRegions.map { e -> RegionRequestDto(e.region.seq, e.priority) }.toList()
        }

        if (ObjectUtils.isEmpty(request.searchOptions.interestList)) {
            // TODO: UserInterest 조회 메서드 생성이 끝나면 여기 완성하자
//             val userInterestDto = userInterestService.findUserInterestList(user);
        }

        val data = clubService.search(request)
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
     *
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
    fun getMyClubList(@AuthUser user: User, @RequestBody searchOptions: PageOption): ResponseDto<Page<ClubUserDto>> {
        return ResponseDto(data = clubService.getUserClubList(user, searchOptions))
    }
}