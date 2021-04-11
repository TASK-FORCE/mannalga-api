package com.taskforce.superinvention.app.domain.club.user

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.role.ClubUserRole
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.config.test.MockTest
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ClubUserServiceTest: MockTest() {

    val clubUserRepository = mockk<ClubUserRepository>()
    val clubUserService    = ClubUserService(clubUserRepository)

    val clubSeq = 555L
    val userSeq = 111L
    val clubUserSeq = 222L
    val user = User("cute eric").apply { seq = userSeq }
    val club = Club(
        "cool eric", "asdasd", 10L, "asdasd"
    ).apply { seq = clubSeq }

    @Test
    fun `모임에 미가입한 유저가 모임을 조회하였을 때는 null을 반환한다`() {
        val clubSeq = 555L
        val user = mockk<User>()

        every { clubUserRepository.findClubUserWithRole(clubSeq, user) }.returns(null)
        val clubUserStatusDto = clubUserService.getClubUserDetails(user, clubSeq)

        assertNull(clubUserStatusDto)
    }

    @Test
    fun `모임에 가입한 모임원이 모임을 조회하였을 때 모임원 권한이 CLUB_MEMBER로 와야한다`() {
        val clubUser = ClubUser(club, user)
            .apply { seq = clubUserSeq }
            .apply { clubUserRoles = mutableSetOf(ClubUserRole(this, Role(Role.RoleName.CLUB_MEMBER, mockk(), 3))) }

        every { clubUserRepository.findClubUserWithRole(clubSeq, user) }.returns(clubUser)
        val clubUserStatusDto = clubUserService.getClubUserDetails(user, clubSeq)

        assertNotNull(clubUserStatusDto)
        assertEquals(userSeq, clubUserStatusDto!!.userSeq)
        assertEquals(clubUserSeq, clubUserStatusDto.clubUserSeq)
        assertEquals(1, clubUserStatusDto.role.size)
        assertEquals(Role.RoleName.CLUB_MEMBER, clubUserStatusDto.role[0])
    }
    
    @Test
    fun `모임에서 탈퇴된 회원이 조회했을 때, 모임원 권한이 MEMBER로 나와야 한다`() {
        val clubUser = ClubUser(club, user)
            .apply { seq = clubUserSeq }
            .apply { clubUserRoles = mutableSetOf(ClubUserRole(this, Role(Role.RoleName.MEMBER, mockk(), 3))) }

        every { clubUserRepository.findClubUserWithRole(clubSeq, user) }.returns(clubUser)
        val clubUserStatusDto = clubUserService.getClubUserDetails(user, clubSeq)

        assertNotNull(clubUserStatusDto)
        assertEquals(userSeq, clubUserStatusDto!!.userSeq)
        assertEquals(clubUserSeq, clubUserStatusDto.clubUserSeq)
        assertEquals(1, clubUserStatusDto.role.size)
        assertEquals(Role.RoleName.MEMBER, clubUserStatusDto.role[0])
    }

    @Test
    fun `모임에서 강퇴된 회원이 조회했을 때, 모임원 권한이 NONE으로 나와야 한다`() {
        val clubUser = ClubUser(club, user)
            .apply { seq = clubUserSeq }
            .apply { clubUserRoles = mutableSetOf(ClubUserRole(this, Role(Role.RoleName.NONE, mockk(), 3))) }

        every { clubUserRepository.findClubUserWithRole(clubSeq, user) }.returns(clubUser)
        val clubUserStatusDto = clubUserService.getClubUserDetails(user, clubSeq)

        assertNotNull(clubUserStatusDto)
        assertEquals(userSeq, clubUserStatusDto!!.userSeq)
        assertEquals(clubUserSeq, clubUserStatusDto.clubUserSeq)
        assertEquals(1, clubUserStatusDto.role.size)
        assertEquals(Role.RoleName.NONE, clubUserStatusDto.role[0])
    }
}
