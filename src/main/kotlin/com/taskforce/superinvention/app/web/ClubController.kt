package com.taskforce.superinvention.app.web

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.domain.user.userInterest.UserInterestService
import com.taskforce.superinvention.app.domain.user.userState.UserStateService
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.club.*
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.state.StateRequestDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("clubs")
class ClubController(
        val clubService: ClubService,
        val userStateService: UserStateService,
        val userInterestService: UserInterestService
) {
    @GetMapping("/{seq}")
    fun getClubBySeq(@PathVariable seq : Long): ResponseDto<Club?> {
        val data = clubService.getClubBySeq(seq)
        return ResponseDto(data = data)
    }

    @GetMapping("/{seq}/users")
    fun getClubUser(@PathVariable seq : Long): ResponseDto<ClubUserDto?> {
        val data = clubService.getClubUserDto(seq)
        return ResponseDto(data = data)
    }

    @PostMapping("/{clubSeq}/users")
    @ResponseStatus(HttpStatus.CREATED)
    fun addClubUser(@AuthUser user: User, @PathVariable("clubSeq") clubSeq: Long): ResponseDto<Any> {
        val club = clubService.getClubBySeq(clubSeq)
        clubService.addClubUser(club, user);
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
        clubService.addClub(club, user, request.interestList, request.stateList)

        return ResponseDto(data = ResponseDto.EMPTY, message = "")
    }

    /**
     * 모임리스트 검색
     * @author eric
     */
    @PostMapping("/search")
    fun getClubList(@AuthUser user: User, @RequestBody request: ClubSearchRequestDto): ResponseDto<Page<ClubWithStateInterestDto>> {
        if (ObjectUtils.isEmpty(request.searchOptions.stateList)) {
            val userStateDto = userStateService.findUserStateList(user)
            request.searchOptions.stateList = userStateDto.userStates.map { e -> StateRequestDto(e.state.seq, e.priority) }.toList()
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
    fun changeClubInterest(@AuthUser user: User, @PathVariable clubSeq: Long, @RequestBody clubInterests: Set<InterestRequestDto>): ResponseDto<ClubWithStateInterestDto> {
        clubService.changeClubInterests(user, clubSeq, clubInterests)
        val data = clubService.getClubWithPriorityDto(clubSeq)
        return ResponseDto(data = data)
    }
}