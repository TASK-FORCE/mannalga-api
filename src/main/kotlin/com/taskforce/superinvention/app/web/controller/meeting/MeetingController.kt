package com.taskforce.superinvention.app.web.controller.meeting

import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.meeting.MeetingService
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingRequestDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingGroupDto
import com.taskforce.superinvention.common.config.argument.resolver.auth.AuthUser
import com.taskforce.superinvention.common.exception.BizException
import com.taskforce.superinvention.common.exception.club.UserIsNotClubMemberException
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.security.access.annotation.Secured
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/clubs/{clubSeq}/meetings")
@Validated
class MeetingController(
        var meetingService: MeetingService,
        var roleService: RoleService,
        var clubService: ClubService
) {

    val userIsNotClubMemberException = UserIsNotClubMemberException()

    @GetMapping
    @Secured(Role.MEMBER)
    fun getAllMeeting(@AuthUser user: User,
                      @PathVariable clubSeq: Long,
                      pageable: Pageable): ResponseDto<PageDto<MeetingGroupDto>> {

        val clubUser = clubService.getClubUser(clubSeq, user)

        return ResponseDto(meetingService.getMeetingsWithGroup(clubSeq, pageable, clubUser?.seq))
    }

    @GetMapping("/{meetingSeq}")
    fun getMeeting(@AuthUser user: User,
                   @PathVariable meetingSeq: Long,
                   @PathVariable clubSeq: Long): ResponseDto<MeetingDto> {
        val clubUser = clubService.getClubUser(clubSeq, user)
        return ResponseDto(meetingService.getMeeting(meetingSeq, clubUser?.seq!!))
    }

    @PostMapping
    @Secured(Role.MEMBER)
    fun createMeeting(@AuthUser user: User,
                      @PathVariable clubSeq: Long,
                      @RequestBody @Valid meetingRequestDto: MeetingRequestDto): ResponseDto<MeetingDto> {

        // check auth
        val clubUser = clubService.getClubUser(clubSeq, user)
                ?: throw BizException("모임원이 아닙니다!", HttpStatus.UNAUTHORIZED)

        if (!roleService.hasClubManagerAuth(clubUser)) {
            throw BizException("매니저 이상의 권한이 필요합니다.", HttpStatus.UNAUTHORIZED)
        }

        val data = meetingService.createMeeting(meetingRequestDto, clubUser.seq!!)

        return ResponseDto(data)
    }

    @PutMapping("/{meetingSeq}")
    @Secured(Role.MEMBER)
    fun modifyMeeting(@AuthUser user: User,
                      @PathVariable clubSeq: Long,
                      @PathVariable meetingSeq: Long,
                      @RequestBody @Valid meetingRequestDto: MeetingRequestDto): ResponseDto<MeetingDto> {
        // check auth
        val clubUser = clubService.getClubUser(clubSeq, user)
                ?: throw BizException("모임원이 아닙니다!", HttpStatus.UNAUTHORIZED)

        if (meetingService.getMeeting(meetingSeq, clubUser.seq).regClubUser.seq != clubUser.seq) {
            throw BizException("만남은 작성자만 수정할 수 있습니다.", HttpStatus.UNAUTHORIZED)
        }

        meetingService.checkClubMeeting(clubSeq, meetingSeq)

        return ResponseDto(meetingService.modifyMeeting(meetingSeq, meetingRequestDto, clubUser))
    }

    @DeleteMapping("/{meetingSeq}")
    @Secured(Role.MEMBER)
    fun deleteMeeting(@AuthUser user: User,
                      @PathVariable clubSeq: Long,
                      @PathVariable meetingSeq: Long): ResponseDto<String> {
        // check auth
        val clubUser = clubService.getClubUser(clubSeq, user)
                ?: throw BizException("모임원이 아닙니다!", HttpStatus.UNAUTHORIZED)

        if (!roleService.hasClubManagerAuth(clubUser)) {
            throw BizException("매니저 이상의 권한이 필요합니다.", HttpStatus.UNAUTHORIZED)
        }

        meetingService.checkClubMeeting(clubSeq, meetingSeq)
        meetingService.deleteMeeting(meetingSeq)
        return ResponseDto(data = ResponseDto.EMPTY, message = "")
    }

}