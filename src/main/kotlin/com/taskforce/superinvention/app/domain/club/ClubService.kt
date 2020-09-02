package com.taskforce.superinvention.app.domain.club

import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.user.User
import com.taskforce.superinvention.app.web.dto.club.ClubUserDto
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.lang.RuntimeException

@Service
class ClubService(
        private var clubRepository: ClubRepository,
        private var clubRepositorySupport: ClubRepositorySupport,
        private var clubUserRepository: ClubUserRepository,
        private var clubUserRepositorySupport: ClubUserRepositorySupport,
        private var roleService: RoleService
) {
    fun getClubBySeq(seq: Long): Club? {
        return clubRepositorySupport.findBySeq(seq)
    }

    fun getClubUserDto(clubSeq: Long): ClubUserDto? {
        val clubUsers = clubUserRepositorySupport.findByClubSeq(clubSeq)
        return ClubUserDto( clubUsers[0].club, clubUsers.map{ e -> e.user}.toList() )
    }

    fun retrieveClubs(keyword: String): List<Club>? {
        return clubRepositorySupport.findByKeyword(keyword)
    }

    /**
     * 새로운 모임을 생성한다.
     * 1. 모임 생성
     * 2. 생성한 유저가 해당 모임에 들어감
     * 3. 생성한 유저에게 모임장 권한을 줌 (Todo)
     */
    @Transactional
    fun addClub(club:Club, superUser: User) {
        val savedClub = clubRepository.save(club)   // 1. 모임 생성
        val superUserClub = ClubUser(savedClub, superUser)   // 2. 생성한 유저가 해당 모임에 들어감
        clubUserRepository.save(superUserClub)

        // TODO:: 3. 생성한 유저에게 모임장 권한을 줌
    }

    @Transactional
    fun getClubUserList(club: Club): List<ClubUser> {
        return clubUserRepository.findByClub(club);
    }

    @Transactional
    fun addClubUser(club: Club, user: User) {
        val clubUserList = getClubUserList(club)
        if (clubUserList.size >= club.maximumNumber) {
            throw IndexOutOfBoundsException("모임 최대 인원을 넘어, 회원가입이 불가합니다.")
        }
        if (clubUserList.map { cu -> cu.user }.contains(user)) {
            throw RuntimeException("이미 가입한 모임입니다.")
        }
        val clubUser = ClubUser(club = club, user = user)
        clubUserRepository.save(clubUser)
    }
}