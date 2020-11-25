package com.taskforce.superinvention.app.domain.meeting

import com.taskforce.superinvention.app.domain.club.QClub
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface MeetingApplicationRepository : JpaRepository<MeetingApplication, Long>, MeetingApplicationRepositoryCustom {
    fun findByClubUserAndMeeting(clubUser: ClubUser, meeting: Meeting): MeetingApplication?
    fun findByMeeting(meeting: Optional<Meeting>): List<MeetingApplication>
}


interface MeetingApplicationRepositoryCustom {

}

@Repository
class MeetingApplicationRepositoryImpl : QuerydslRepositorySupport(MeetingApplication::class.java), MeetingApplicationRepositoryCustom{

}