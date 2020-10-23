package com.taskforce.superinvention.app.web.controller.meeting

import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.meeting.MeetingService
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.common.request.PageOption
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingAddRequestDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import com.taskforce.superinvention.common.exception.BizException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clubs/{clubSeq}/meetings")
class MeetingController(
        var meetingService: MeetingService,
        var roleService: RoleService,
        var clubService: ClubService
) {

    @GetMapping
    @Secured(Role.MEMBER)
    fun getAllMeeting(@AuthUser user: User,
                      @PathVariable clubSeq: Long,
                      @ModelAttribute pageOption: PageOption): ResponseDto<Page<MeetingDto>> {
        if (!roleService.hasClubMemberAuth(clubSeq, user)) {
            throw BizException("모임원이 아닙니다!", HttpStatus.UNAUTHORIZED)
        }

        val pageable: Pageable = PageRequest.of(pageOption.page, pageOption.size)
        return ResponseDto(meetingService.getMeeting(clubSeq, pageable))
    }

    @PostMapping
    @Secured(Role.MEMBER)
    fun createMeeting(@AuthUser user: User,
                      @PathVariable clubSeq: Long,
                      @RequestBody meetingAddRequestDto: MeetingAddRequestDto): ResponseDto<MeetingDto> {

        // check auth
        val clubUser = clubService.getClubUser(clubSeq, user)
                ?: throw BizException("모임원이 아닙니다!", HttpStatus.UNAUTHORIZED)
        if (!roleService.hasClubManagerAuth(clubUser)) {
            throw BizException("매니저 이상의 권한이 필요합니다.", HttpStatus.UNAUTHORIZED)
        }

        // check validation
        if (meetingAddRequestDto.startTimestamp.isBefore(meetingAddRequestDto.endTimestamp))
            throw BizException("만남 종료 시간은 시작시간 이후여야 합니다.", HttpStatus.BAD_REQUEST)

        return ResponseDto(meetingService.createMeeting(meetingAddRequestDto, clubUser.seq!!))
    }

}