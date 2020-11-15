package com.taskforce.superinvention.app.web.controller.meeting

import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.club.user.ClubUserService
import com.taskforce.superinvention.app.domain.meeting.MeetingService
import com.taskforce.superinvention.app.domain.role.ClubUserRole
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingApplicationDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/clubs/{clubSeq}/meetings/{meetingSeq}/applications")
class MeetingApplicationController(
        val meetingService: MeetingService,
        val roleService: RoleService,
        val clubService: ClubService,
        val clubUserService: ClubUserService
) {
    @PostMapping
    @Transactional
    fun meetingApplication(@AuthUser user: User, @PathVariable("clubSeq") clubSeq: Long, @PathVariable meetingSeq: Long): ResponseDto<MeetingApplicationDto> {
        // 권한체크
        val clubUser = clubService.getClubUser(clubSeq, user)?: throw BizException("모임원이 아닙니다.", HttpStatus.FORBIDDEN)

        return ResponseDto(meetingService.application(clubUser, meetingSeq))
    }

    @DeleteMapping("/{meetingApplicationSeq}")
    fun cancelApplication(@AuthUser user: User,
                                  @PathVariable("clubSeq") clubSeq: Long,
                                  @PathVariable meetingSeq: Long,
                                  @PathVariable meetingApplicationSeq: Long): ResponseDto<MeetingApplicationDto> {
        // 권한체크
        val clubUser = clubService.getClubUser(clubSeq, user)?: throw BizException("모임원이 아닙니다.", HttpStatus.FORBIDDEN)
        val meetingApplication = meetingService.getMeetingApplication(meetingApplicationSeq)

        if (!meetingService.isRegUser(meetingApplication, user))
            throw BizException("신청한 유저가 아닙니다.", HttpStatus.FORBIDDEN)

        return ResponseDto(meetingService.applicationCancel(clubUser, meetingApplicationSeq))
    }


    @GetMapping("/{meetingApplicationSeq}")
    fun getMeetingApplicationInfo(@AuthUser user: User,
                                  @PathVariable("clubSeq") clubSeq: Long,
                                  @PathVariable meetingSeq: Long,
                                  @PathVariable meetingApplicationSeq: Long): ResponseDto<MeetingApplicationDto> {
        // 권한체크
        val clubUser = clubService.getClubUser(clubSeq, user)?: throw BizException("모임원이 아닙니다.", HttpStatus.FORBIDDEN)
        val meetingApplication = meetingService.getMeetingApplication(meetingApplicationSeq)
        if (!meetingService.isRegUser(meetingApplication, user))
            throw BizException("신청한 유저가 아닙니다.", HttpStatus.FORBIDDEN)

        return ResponseDto(meetingService.getMeetingApplication(meetingApplicationSeq))
    }
}