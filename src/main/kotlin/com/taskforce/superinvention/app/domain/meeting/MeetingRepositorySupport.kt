package com.taskforce.superinvention.app.domain.meeting

import com.querydsl.jpa.JPQLQuery
import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.QClub
import com.taskforce.superinvention.app.domain.club.QClub.club
import com.taskforce.superinvention.app.domain.meeting.QMeeting.meeting
import com.taskforce.superinvention.app.domain.meeting.QMeetingApplication.meetingApplication
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Repository
class MeetingRepositorySupport : QuerydslRepositorySupport(Meeting::class.java){

    @Transactional(readOnly = true)
    fun getMeeting(clubSeq: Long, pageable: Pageable): Page<Meeting> {
        val query = from(meeting)
                .join(meeting.club, club)
                .leftJoin(meeting.meetingApplications, meetingApplication)
                .fetchJoin()
                .where(club.seq.eq(clubSeq)
                        , meeting.deleteFlag.isFalse
                )

        val fetchResult = query
                .groupBy(meeting)
                .orderBy(meeting.startTimestamp.desc())
                .offset(pageable.offset)
                .limit(pageable.pageSize.toLong())
                .fetchResults()

        return PageImpl(fetchResult.results, pageable, fetchResult.total)
    }

}