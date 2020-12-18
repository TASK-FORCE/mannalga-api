package com.taskforce.superinvention.app.domain.meeting

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.meeting.MeetingDto
import com.taskforce.superinvention.common.exception.BizException
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.util.*
import kotlin.properties.Delegates


internal class MeetingServiceTest {

    var clubSeq by Delegates.notNull<Long>()

    var clubUserSeq by Delegates.notNull<Long>()

    var regClubUserSeq by Delegates.notNull<Long>()

    lateinit var pageable: Pageable

    lateinit var meeting:Meeting

    lateinit var meetingService: MeetingService

    lateinit var meetingRepositoryImpl: MeetingRepositoryImpl

    lateinit var meetingRepository: MeetingRepository


    @BeforeEach
    fun init() {
        clubSeq = 123123
        clubUserSeq = 6742
        regClubUserSeq = 4444

        var mockClub = mockk<Club>(relaxed = true).apply {
            seq = 1231254
            name = "CLUB NAME"
            description = "description"
            maximumNumber = 50
            userCount = 10
        }

        pageable = PageRequest.of(0, 20)

        meeting = Meeting(
                title = "title",
                content = "content",
                startTimestamp = LocalDateTime.parse("1995-12-27T02:00:00"),
                endTimestamp = LocalDateTime.parse("1995-12-27T07:00:00"),
                club = mockClub,
                deleteFlag = false,
                maximumNumber = 20,
                regClubUser = ClubUser(
                        club = mockClub,
                        user = mockk<User>(relaxed = true).apply {
                            seq = 613
                        },
                        isLiked = false
                ).apply {
                    seq = regClubUserSeq
                    clubUserRoles = mutableSetOf()
                }
        ).apply {
            seq = 421
            meetingApplications = listOf()
        }

        meetingRepositoryImpl = mockk<MeetingRepositoryImpl>()
        meetingRepository = mockk()
        meetingService = MeetingService(
                meetingRepository = meetingRepository,
                clubService = mockk(),
                meetingApplicationRepository = mockk(),
                meetingRepositoryImpl = meetingRepositoryImpl,
                roleService = mockk()
        )
    }

    @Test
    @DisplayName("모임원이 아닌 유저의 만남 조회")
    fun getMeetingsTestWithoutClubUserRole() {
        // given
        every { meetingRepositoryImpl.getMeetings(clubSeq, pageable) }.returns(PageImpl(listOf(meeting), pageable, 1))

        // when
        val response = meetingService.getMeetings(clubSeq, pageable, null)

        // then
        assertEquals(1, response.content.size)
        assertFalse(response.first().isCurrentUserRegMeeting)
        assertFalse(response.first().isCurrentUserApplicationMeeting)
        assertEquals("title", response.first().title)
    }


    @Test
    @DisplayName("모임원인 유저의 만남 조회")
    fun getMeetingsTestWitClubUserRole() {
        // given
        meeting.meetingApplications = listOf(
                MeetingApplication(
                        clubUser = ClubUser(
                                club = mockk(),
                                user = mockk<User>(relaxed = true).apply {
                                    seq = 61236
                                },
                                isLiked = false
                        ).apply {
                            seq = clubUserSeq
                            user = user
                        },
                        meeting = meeting,
                        deleteFlag = false
                ).apply {
                    seq = 5
                }
        )

        every { meetingRepositoryImpl.getMeetings(clubSeq, pageable) }.returns(PageImpl(listOf(meeting), pageable, 1))

        // when
        val response = meetingService.getMeetings(clubSeq, pageable, clubUserSeq)

        // then
        assertEquals(1, response.content.size)
        assertFalse(response.first().isCurrentUserRegMeeting)
        assertTrue(response.first().isCurrentUserApplicationMeeting)
    }

    @Test
    @DisplayName("모임원이며 만남 생성을 한 유저의 만남 조회")
    fun getMeetingsTestWitGenerateMeetingUser() {
        // given
        meeting.meetingApplications = listOf(
                MeetingApplication(
                        clubUser = ClubUser(
                                club = mockk(),
                                user = mockk<User>(relaxed = true).apply {
                                    seq = 61236
                                },
                                isLiked = false
                        ).apply {
                            seq = regClubUserSeq
                            user = user
                        },
                        meeting = meeting,
                        deleteFlag = false
                ).apply {
                    seq = 5
                }
        )

        every { meetingRepositoryImpl.getMeetings(clubSeq, pageable) }.returns(PageImpl(listOf(meeting), pageable, 1))

        // when
        val response = meetingService.getMeetings(clubSeq, pageable, regClubUserSeq)

        // then
        assertEquals(1, response.content.size)
        assertTrue(response.first().isCurrentUserRegMeeting)
        assertTrue(response.first().isCurrentUserApplicationMeeting)
    }

    @Test
    @DisplayName("모임원이 아닌 유저의 모임 개별건 조회")
    fun getMeetingTestWithoutClubUserRole() {
        // given
        every { meetingRepository.findById(meeting.seq!!) }.returns(Optional.of(meeting))

        // when
        val response = meetingService.getMeeting(meeting.seq!!, clubUserSeq)

        // then
        assertEquals(meeting.seq!!, response.seq)
        assertEquals(meeting.content, response.content)
    }
    
    @Test
    @DisplayName("존재하지 않는 모임 개별건 조회")
    fun getMeetingTestNotFountSeq() {
        // given
        every { meetingRepository.findById(meeting.seq!!) }.returns(Optional.empty())

        // when, then
        assertThrows(BizException::class.java) {
            meetingService.getMeeting(meeting.seq!!, clubUserSeq)
        }
    }
}