package com.taskforce.superinvention.app.web

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.user.user.User
import com.taskforce.superinvention.app.domain.user.userInterest.UserInterestService
import com.taskforce.superinvention.app.domain.user.userState.UserStateService
import com.taskforce.superinvention.app.web.dto.club.*
import com.taskforce.superinvention.app.web.dto.interest.InterestRequestDto
import com.taskforce.superinvention.app.web.dto.state.StateRequestDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import org.springframework.security.access.annotation.Secured
import org.springframework.util.ObjectUtils
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("clubs")
class ClubController(
        val clubService : ClubService,
        val userStateService: UserStateService,
        val userInterestService: UserInterestService
) {
    @GetMapping("/{seq}")
    fun getClubBySeq(@PathVariable seq : Long): Club? {
        return clubService.getClubBySeq(seq)
    }

    @GetMapping("/{seq}/users")
    fun getClubUser(@PathVariable seq : Long): ClubUserDto? {
        return clubService.getClubUserDto(seq)
    }

    @PostMapping("/{clubSeq}/users")
    fun addClubUser(@AuthUser user: User, @PathVariable("clubSeq") clubSeq: Long) {
        val club = clubService.getClubBySeq(clubSeq)
        if (club == null) throw NullPointerException("존재하지 않는 모임입니다")
        clubService.addClubUser(club, user);
    }

    /**
     * 모임 생성
     * @author eric
     */
    @Secured("ROLE_USER")
    @PostMapping
    fun addClub(@AuthUser user: User, @RequestBody request: ClubAddRequestDto) {
        val club = Club(name = request.name, description = request.description, maximumNumber = request.maximumNumber, mainImageUrl = request.mainImageUrl)
        clubService.addClub(club, user, request.interestList, request.stateList)
    }

    /**
     * 모임리스트 검색
     * @author eric
     */
    @GetMapping
    fun getClubList(@AuthUser user: User, @RequestBody request: ClubSearchRequestDto): List<ClubWithStateInterestDto> {
        if (ObjectUtils.isEmpty(request.searchOptions.stateList)) {
            val userStateDto = userStateService.findUserStateList(user)
            request.searchOptions.stateList = userStateDto.userStates.map { e -> StateRequestDto(e.state.seq, e.priority) }.toList()
        }

        if (ObjectUtils.isEmpty(request.searchOptions.interestList)) {
            // TODO: UserInterest 조회 메서드 생성이 끝나면 여기 완성하자
//             val userInterestDto = userInterestService.findUserInterestList(user);
        }

        return clubService.search(request)
    }

    @PutMapping("/{seq}/interests")
    fun changeClubInterest(@AuthUser user: User, @PathVariable clubSeq: Long,  @RequestBody clubInterests: Set<InterestRequestDto>): ClubDto {
        var club = clubService.getClubBySeq(clubSeq)
        clubService.changeClubInterests(user, club, clubInterests)

        club = clubService.getClubBySeq(clubSeq)
        return ClubDto(clubService.getClubBySeq(clubSeq), club.clubUser.size.toLong())
    }
}