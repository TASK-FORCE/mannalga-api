package com.taskforce.superinvention.app.domain.meeting

import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.role.RoleService
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class MeetingService(
        var meetingRepository: MeetingRepository,
        var meetingRepositorySupport: MeetingRepositorySupport,
        var roleService: RoleService,
        var clubService: ClubService
) {

    fun getMeeting(clubSeq: Long, pageable: Pageable): Page<Meeting> {
        return meetingRepositorySupport.getMeeting(clubSeq, pageable)
    }


}