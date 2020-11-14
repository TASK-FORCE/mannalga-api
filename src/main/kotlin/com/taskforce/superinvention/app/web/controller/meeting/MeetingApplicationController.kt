package com.taskforce.superinvention.app.web.controller.meeting

import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.club.user.ClubUserService
import com.taskforce.superinvention.app.domain.meeting.MeetingService
import com.taskforce.superinvention.app.domain.role.ClubUserRole
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.meeting.MeetingApplicationDto
import com.taskforce.superinvention.common.config.argument.auth.AuthUser
import com.taskforce.superinvention.common.exception.BizException
import org.springframework.http.HttpStatus
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
    fun meetingApplication(@AuthUser user: User, @PathVariable("clubSeq") clubSeq: Long, @PathVariable meetingSeq: Long): MeetingApplicationDto {
        // 권한체크
        val clubUser = clubService.getClubUser(clubSeq, user)?: throw BizException("모임원이 아닙니다.", HttpStatus.FORBIDDEN)
        val hasClubUserRole = clubUser.clubUserRoles.map { e -> e.role.name }.contains(Role.fromRoleName(Role.CLUB_MEMBER))
        if (!hasClubUserRole) throw BizException("모임원이 아닙니다.", HttpStatus.FORBIDDEN)

        return meetingService.application(clubUser, meetingSeq)
    }

    @GetMapping("/{meetingApplicationSeq}")
    fun getMeetingApplicationInfo(@AuthUser user: User,
                                  @PathVariable("clubSeq") clubSeq: Long,
                                  @PathVariable meetingSeq: Long,
                                  @PathVariable meetingApplicationSeq: Long): MeetingApplicationDto {
        // 권한체크
        val clubUser = clubService.getClubUser(clubSeq, user)?: throw BizException("모임원이 아닙니다.", HttpStatus.FORBIDDEN)
        val hasClubUserRole = clubUser.clubUserRoles.map { e -> e.role.name }.contains(Role.fromRoleName(Role.CLUB_MEMBER))
        if (!hasClubUserRole) throw BizException("모임원이 아닙니다.", HttpStatus.FORBIDDEN)
        val meetingApplication = meetingService.getMeetingApplication(meetingApplicationSeq)
        if (meetingApplication.clubUser.userSeq != user.seq)
            throw BizException("신청한 유저가 아닙니다.", HttpStatus.FORBIDDEN)

        return meetingService.applicationCancel(clubUser, meetingApplicationSeq)
    }



}