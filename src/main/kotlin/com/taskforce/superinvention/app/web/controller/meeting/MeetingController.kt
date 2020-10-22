package com.taskforce.superinvention.app.web.controller.meeting

import com.taskforce.superinvention.app.domain.meeting.Meeting
import com.taskforce.superinvention.app.domain.meeting.MeetingService
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
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
        var roleService: RoleService
) {

    @GetMapping
    @Secured(Role.MEMBER)
    fun getAllMeeting(@AuthUser user: User,
                      @PathVariable clubSeq: Long,
                      @RequestParam(defaultValue = "0") page: Int,
                      @RequestParam(defaultValue = "10") size:Int): Page<Meeting> {
        if (!roleService.hasClubMemberAuth(clubSeq, user.seq!!)) {
            throw BizException("모임원이 아닙니다!", HttpStatus.UNAUTHORIZED)
        }

        val pageable: Pageable = PageRequest.of(page, size)
        return meetingService.getMeeting(clubSeq, pageable)
    }

}