package com.taskforce.superinvention.app.domain.meeting

import com.taskforce.superinvention.app.domain.club.ClubService
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.common.PageDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingApplicationDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingApplicationStatusDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingRequestDto
import com.taskforce.superinvention.app.web.dto.meeting.MeetingDto
import com.taskforce.superinvention.common.exception.BizException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MeetingService(
        var meetingRepository: MeetingRepository,
        var meetingRepositoryImpl: MeetingRepositoryImpl,
        var roleService: RoleService,
        var clubService: ClubService,
        var meetingApplicationRepository: MeetingApplicationRepository
) {

    val meetingNotFountException = BizException("존재하지 않는 만남입니다", HttpStatus.NOT_FOUND)
    val meetingApplicationNotFoundException = BizException("존재하지 않는 만남 신청입니다.",HttpStatus.NOT_FOUND)

    fun getNotDeletedMeetingEntity(meetingSeq: Long): Meeting = meetingRepository.findById(meetingSeq)
        .filter{!it.deleteFlag}
        .orElseThrow{meetingNotFountException}
        .apply { meetingApplications = meetingApplications.filter { !it.deleteFlag } }

    @Transactional(readOnly = true)
    fun getMeetings(clubSeq: Long, pageable: Pageable, currentClubUserSeq: Long?): PageDto<MeetingDto> {

        val resultPage = meetingRepositoryImpl
            .getMeetings(clubSeq, pageable)
            .map { e -> MeetingDto(e, currentClubUserSeq)
                .apply {
                    meetingApplications = meetingApplications.filterNot { it.deleteFlag }
                }
            }
        return PageDto(resultPage)
    }

    @Transactional
    fun createMeeting(meetingRequestDto: MeetingRequestDto, clubUserSeq: Long): MeetingDto {
        // check validation
        val clubUser = clubService.getClubUserByClubUserSeq(clubUserSeq)
                ?: throw BizException("존재하지 않는 모임원입니다", HttpStatus.INTERNAL_SERVER_ERROR)

        if (meetingRequestDto.startTimestamp.isAfter(meetingRequestDto.endTimestamp))
            throw BizException("만남 종료 시간은 시작시간 이후여야 합니다.", HttpStatus.BAD_REQUEST)

        meetingRequestDto.cost?.let { if(it < 0)
            throw BizException("만남 금액은 0원 이상이어야 합니다. ${it}원으로 설정할 수 없습니다.") }


        val meeting = Meeting(
                title = meetingRequestDto.title,
                content = meetingRequestDto.content,
                startTimestamp = meetingRequestDto.startTimestamp,
                endTimestamp = meetingRequestDto.endTimestamp,
                club = clubUser.club,
                deleteFlag = false,
                maximumNumber = meetingRequestDto.maximumNumber,
                regClubUser = clubUser,
                region = meetingRequestDto.region,
                regionURL = meetingRequestDto.regionURL,
                cost = meetingRequestDto.cost
        )
        return MeetingDto(meetingRepository.save(meeting), clubUser.seq!!)
    }

    @Transactional
    fun modifyMeeting(meetingId: Long, meetingRequestDto: MeetingRequestDto, currentClubUser: ClubUser): MeetingDto {
        val meeting: Meeting = meetingRepository.findById(meetingId).orElseThrow { meetingNotFountException }
        meeting.title = meetingRequestDto.title
        meeting.content = meetingRequestDto.content
        meeting.startTimestamp = meetingRequestDto.startTimestamp
        meeting.endTimestamp = meetingRequestDto.endTimestamp
        meeting.maximumNumber = meetingRequestDto.maximumNumber
        meeting.region = meetingRequestDto.region
        meeting.regionURL = meetingRequestDto.regionURL
        meeting.cost = meetingRequestDto.cost

        return MeetingDto(meeting, currentClubUser.seq!!)
    }

    @Transactional
    fun checkClubMeeting(clubSeq: Long, meetingSeq: Long) {
        val meeting = getNotDeletedMeetingEntity(meetingSeq)
        if (meeting.club.seq!! != clubSeq)
            throw BizException("해당 모임의 만남이 아닙니다", HttpStatus.FORBIDDEN)
    }

    @Transactional
    fun deleteMeeting(meetingSeq: Long) {
        val meeting = getNotDeletedMeetingEntity(meetingSeq)
        meeting.deleteFlag = true
    }

    /**
     * 만남 신청
     */
    @Transactional
    fun application(clubUser: ClubUser, meetingSeq: Long): MeetingApplicationDto {
        val meeting = getNotDeletedMeetingEntity(meetingSeq)

        // 참석자 최대인원 확인
        if (meeting.maximumNumber != null && meeting.maximumNumber!! <= meeting.meetingApplications.filter { e -> !e.deleteFlag }.groupBy { e -> e.clubUser }.count())
            throw BizException("인원이 다 차서 신청할 수 없습니다. 최대 인원은 ${meeting.maximumNumber}명 입니다.", HttpStatus.CONFLICT)

        // 이미 신청하였으면 신청 못하게 막는다
        val application = meetingApplicationRepository.findByClubUserAndMeeting(clubUser, meeting)
        if (application != null)
            return if (application.deleteFlag)
                MeetingApplicationDto(application.apply { this.deleteFlag = false })
            else
                throw BizException("이미 신청한 만남입니다. 신청한 만남 id: ${application.seq}", HttpStatus.CONFLICT)

        // 신청
        val meetingApplication = MeetingApplication(clubUser, meeting, false)
        return MeetingApplicationDto(meetingApplicationRepository.save(meetingApplication))
    }

    /**
     * 만남신청 취소
     */
    @Transactional
    fun applicationCancel(clubUser: ClubUser, meetingApplicationSeq: Long): MeetingApplicationDto{
        val meetingApplication = meetingApplicationRepository
            .findById(meetingApplicationSeq)
            .filter{!it.deleteFlag}
            .orElseThrow{ meetingApplicationNotFoundException }
        if (meetingApplication.deleteFlag)
            throw BizException("이미 취소한 만남 신청입니다", HttpStatus.CONFLICT)
        return MeetingApplicationDto(meetingApplication.apply { this.deleteFlag = true })
    }


    /**
     * 신청한 만남 정보
     */
    @Transactional
    fun getMeetingApplication(meetingApplicationSeq: Long): MeetingApplicationDto {
        val meetingApplication = meetingApplicationRepository
            .findById(meetingApplicationSeq)
            .filter{!it.deleteFlag}
            .orElseThrow { meetingApplicationNotFoundException }
        return MeetingApplicationDto(meetingApplication)
    }

    fun isRegUser(meetingApplication: MeetingApplicationDto, user: User): Boolean {
        return meetingApplication.clubUser.user.seq == user.seq
    }

    @Transactional
    fun findMeetingApplication(clubUser: ClubUser, meetingSeq: Long): MeetingApplication {
        return meetingRepository.findMeetingApplicationByUserAndMeetingSeq(clubUser, meetingSeq)
    }

    @Transactional
    fun getMeeting(meetingSeq: Long, clubUserSeq: Long?): MeetingDto {
        return MeetingDto(getNotDeletedMeetingEntity(meetingSeq), clubUserSeq)
    }

    @Transactional
    fun getMeetingApplicationStatus(meetingSeq: Long, clubUser: ClubUser?): MeetingApplicationStatusDto {
        return MeetingApplicationStatusDto(getNotDeletedMeetingEntity(meetingSeq), clubUser?.seq)
    }

}