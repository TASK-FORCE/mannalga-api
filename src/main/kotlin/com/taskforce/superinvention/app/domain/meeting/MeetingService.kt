package com.taskforce.superinvention.app.domain.meeting

import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.web.dto.club.ClubDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingAddRequestDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingDto
import com.taskforce.superinvention.common.exception.BizException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MeetingService(
        var meetingRepository: MeetingRepository,
        var meetingRepositorySupport: MeetingRepositorySupport,
        var roleService: RoleService,
        var clubService: ClubService
) {

    @Transactional(readOnly = true)
    fun getMeeting(clubSeq: Long, pageable: Pageable): Page<MeetingDto> {
        return meetingRepositorySupport.getMeeting(clubSeq, pageable).map { e -> MeetingDto(e) }
    }

    @Transactional
    fun createMeeting(meetingAddRequestDto: MeetingAddRequestDto, clubUserSeq: Long): MeetingDto {
        // check validation
        val clubUser = clubService.getClubUserByClubUserSeq(clubUserSeq)
                ?: throw BizException("존재하지 않는 모임원입니다", HttpStatus.INTERNAL_SERVER_ERROR)

        if (meetingAddRequestDto.startTimestamp.isBefore(meetingAddRequestDto.endTimestamp))
            throw BizException("만남 종료 시간은 시작시간 이후여야 합니다.", HttpStatus.BAD_REQUEST)

        val meeting: Meeting = Meeting(
                title = meetingAddRequestDto.title,
                content = meetingAddRequestDto.content,
                startTimestamp = meetingAddRequestDto.startTimestamp,
                endTimestamp = meetingAddRequestDto.endTimestamp,
                club = clubUser.club,
                deleteFlag = false,
                maximumNumber = meetingAddRequestDto.maximumNumber,
                regClubUser = clubUser
        )
        return MeetingDto(meetingRepository.save(meeting))
    }


}