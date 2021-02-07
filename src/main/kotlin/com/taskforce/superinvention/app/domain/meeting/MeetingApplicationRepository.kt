package com.taskforce.superinvention.app.domain.meeting

import com.taskforce.superinvention.app.domain.club.QClub
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.QClubUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.*

@Repository
interface MeetingApplicationRepository : JpaRepository<MeetingApplication, Long>, MeetingApplicationRepositoryCustom {
    fun findByClubUserAndMeeting(clubUser: ClubUser, meeting: Meeting): MeetingApplication?
    fun findByMeetingIn(meetings: Iterable<Meeting>): List<MeetingApplication>
}


interface MeetingApplicationRepositoryCustom {
    fun findMyLiveMeetingApplication(clubUser: ClubUser): List<MeetingApplication>

}

@Repository
class MeetingApplicationRepositoryImpl : QuerydslRepositorySupport(MeetingApplication::class.java), MeetingApplicationRepositoryCustom{
    override fun findMyLiveMeetingApplication(targetClubUser: ClubUser): List<MeetingApplication> {
        val meeting = QMeeting.meeting
        val meetingApplication = QMeetingApplication.meetingApplication
        val clubUser = QClubUser.clubUser
        return from(meetingApplication)
            .join(meetingApplication.meeting, meeting)
            .join(meetingApplication.clubUser, clubUser)
            .where(clubUser.eq(clubUser), meeting.deleteFlag.isFalse, meeting.endTimestamp.after(LocalDateTime.now()))
            .fetch()
    }
}