package com.taskforce.superinvention.app.domain.meeting

import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.web.dto.meeting.MeetingRequestDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingDto
import com.taskforce.superinvention.common.exception.BizException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

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
    fun createMeeting(meetingRequestDto: MeetingRequestDto, clubUserSeq: Long): MeetingDto {
        // check validation
        val clubUser = clubService.getClubUserByClubUserSeq(clubUserSeq)
                ?: throw BizException("존재하지 않는 모임원입니다", HttpStatus.INTERNAL_SERVER_ERROR)

        if (meetingRequestDto.startTimestamp.isBefore(meetingRequestDto.endTimestamp))
            throw BizException("만남 종료 시간은 시작시간 이후여야 합니다.", HttpStatus.BAD_REQUEST)

        val meeting: Meeting = Meeting(
                title = meetingRequestDto.title,
                content = meetingRequestDto.content,
                startTimestamp = meetingRequestDto.startTimestamp,
                endTimestamp = meetingRequestDto.endTimestamp,
                club = clubUser.club,
                deleteFlag = false,
                maximumNumber = meetingRequestDto.maximumNumber,
                regClubUser = clubUser
        )
        return MeetingDto(meetingRepository.save(meeting))
    }

    @Transactional
    fun modifyMeeting(meetingId: Long, meetingRequestDto: MeetingRequestDto): MeetingDto {
        val meeting: Meeting = meetingRepository.findById(meetingId).orElseThrow { BizException("존재하지 않는 만남입니다", HttpStatus.NOT_FOUND) }
        meeting.title = meetingRequestDto.title
        meeting.content = meetingRequestDto.content
        meeting.startTimestamp = meetingRequestDto.startTimestamp
        meeting.endTimestamp = meetingRequestDto.endTimestamp
        meeting.maximumNumber = meetingRequestDto.maximumNumber

        return MeetingDto(meeting)
    }

    @Transactional
    fun checkClubMeeting(clubSeq: Long, meetingSeq: Long) {
        val meeting = meetingRepository.findById(meetingSeq).orElseThrow { BizException("존재하지 않는 만남입니다", HttpStatus.NOT_FOUND) }
        if (meeting.club.seq!! != clubSeq)
            throw BizException("해당 클럽의 만남이 아닙니다", HttpStatus.FORBIDDEN)
    }

    @Transactional
    fun deleteMeeting(meetingSeq: Long) {
        val meeting = meetingRepository.findById(meetingSeq).orElseThrow { BizException("존재하지 않는 만남입니다", HttpStatus.NOT_FOUND) }
        meeting.deleteFlag = true;
    }


}