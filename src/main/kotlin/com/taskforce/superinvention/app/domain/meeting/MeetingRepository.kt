package com.taskforce.superinvention.app.domain.meeting

import com.taskforce.superinvention.app.domain.club.QClub
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface MeetingRepository : JpaRepository<Meeting, Long>, MeetingRepositoryCustom {
}


interface MeetingRepositoryCustom {

}

@Repository
class MeetingRepositoryImpl : QuerydslRepositorySupport(Meeting::class.java), MeetingRepositoryCustom{

    @Transactional(readOnly = true)
    fun getMeeting(clubSeq: Long, pageable: Pageable): Page<Meeting> {
        val query = from(QMeeting.meeting)
                .join(QMeeting.meeting.club, QClub.club)
                .leftJoin(QMeeting.meeting.meetingApplications, QMeetingApplication.meetingApplication)
                .fetchJoin()
                .where(QClub.club.seq.eq(clubSeq)
                        , QMeeting.meeting.deleteFlag.isFalse
                )

        val fetchResult = query
                .groupBy(QMeeting.meeting)
                .orderBy(QMeeting.meeting.startTimestamp.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetchResults()

        return PageImpl(fetchResult.results, pageable, fetchResult.total)
    }

}