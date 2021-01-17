package com.taskforce.superinvention.app.web.controller.meeting

import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.club.user.ClubUserService
import com.taskforce.superinvention.app.domain.meeting.MeetingService
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingApplicationDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingApplicationStatusDto
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
    fun meetingApplication(@AuthUser user: User, @PathVariable("clubSeq") clubSeq: Long, @PathVariable meetingSeq: Long): ResponseDto<Any> {
        // 권한체크
        val clubUser = clubService.getClubUser(clubSeq, user)?: throw BizException("모임원이 아닙니다.", HttpStatus.FORBIDDEN)
        if (roleService.hasClubMemberAuth(clubUser)) throw BizException("모임원이 아닙니다.", HttpStatus.FORBIDDEN)

        // 만남 신청
        meetingService.application(clubUser, meetingSeq)
        return ResponseDto(ResponseDto.EMPTY)
    }

    @DeleteMapping("/{meetingApplicationSeq}")
    fun cancelApplication(@AuthUser user: User,
                                  @PathVariable("clubSeq") clubSeq: Long,
                                  @PathVariable meetingSeq: Long,
                                  @PathVariable meetingApplicationSeq: Long): ResponseDto<Any> {
        // 권한체크
        val clubUser = clubService.getClubUser(clubSeq, user)?: throw BizException("모임원이 아닙니다.", HttpStatus.FORBIDDEN)
        val meetingApplication = meetingService.getMeetingApplication(meetingApplicationSeq)

        if (!meetingService.isRegUser(meetingApplication, user))
            throw BizException("신청한 유저가 아닙니다.", HttpStatus.FORBIDDEN)

        meetingService.applicationCancel(clubUser, meetingApplicationSeq)

        return ResponseDto(ResponseDto.EMPTY)
    }

    @DeleteMapping
    fun cancelApplicationWithoutSeq(@AuthUser user: User,
                                    @PathVariable("clubSeq") clubSeq: Long,
                                    @PathVariable meetingSeq: Long): ResponseDto<Any> {
        val clubUser = clubService.getClubUser(clubSeq, user)?: throw BizException("모임원이 아닙니다.", HttpStatus.FORBIDDEN)
        var meetingApplication = meetingService.findMeetingApplication(clubUser, meetingSeq)
        return cancelApplication(user, clubSeq, meetingSeq, meetingApplication.seq!!)
    }


    @GetMapping("/{meetingApplicationSeq}")
    fun getMeetingApplicationInfo(@AuthUser user: User,
                                  @PathVariable clubSeq: Long,
                                  @PathVariable meetingSeq: Long,
                                  @PathVariable meetingApplicationSeq: Long): ResponseDto<MeetingApplicationDto> {
        // 권한체크
        val clubUser = clubService.getClubUser(clubSeq, user)?: throw BizException("모임원이 아닙니다.", HttpStatus.FORBIDDEN)
        val meetingApplication = meetingService.getMeetingApplication(meetingApplicationSeq)
        if (!meetingService.isRegUser(meetingApplication, user))
            throw BizException("신청한 유저가 아닙니다.", HttpStatus.FORBIDDEN)

        return ResponseDto(meetingService.getMeetingApplication(meetingApplicationSeq))
    }

    @GetMapping
    fun getMeetingApplicationInfoWithoutSeq(@AuthUser user: User,
                                    @PathVariable("clubSeq") clubSeq: Long,
                                    @PathVariable meetingSeq: Long): ResponseDto<MeetingApplicationDto> {
        val clubUser = clubService.getClubUser(clubSeq, user)?: throw BizException("모임원이 아닙니다.", HttpStatus.FORBIDDEN)
        var meetingApplication = meetingService.findMeetingApplication(clubUser, meetingSeq)
        return getMeetingApplicationInfo(user, clubSeq, meetingSeq, meetingApplication.seq!!)
    }

    @GetMapping("/status")
    fun getMeetingApplicationStatus(@AuthUser user: User,
                                    @PathVariable clubSeq: Long,
                                    @PathVariable meetingSeq: Long): ResponseDto<MeetingApplicationStatusDto> {
        val clubUser = clubService.getClubUser(clubSeq, user)?: throw BizException("모임원이 아닙니다.", HttpStatus.FORBIDDEN)
        val meetingApplicationStatus = meetingService.getMeetingApplicationStatus(meetingSeq, clubUser)
        return ResponseDto(meetingApplicationStatus)
    }


}