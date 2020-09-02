package com.taskforce.superinvention.app.domain.club

import com.taskforce.superinvention.app.domain.role.ClubUserRole
import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.role.RoleService
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.ClubUserDto
import com.taskforce.superinvention.app.web.dto.club.UserClubDto
import org.springframework.stereotype.Service

@Service
class ClubService(
        private var clubRepository: ClubRepository,
        private var clubRepositorySupport: ClubRepositorySupport,
        private var clubUserRepository: ClubUserRepository,
        private var clubUserRepositorySupport: ClubUserRepositorySupport,
        private var roleService: RoleService
) {
    fun getClubBySeq(seq: Long): Club? {
        return clubRepository.findBySeq(seq)
    }

    fun getClubUserList(clubSeq: Long): ClubUserDto? {
        val clubUsers = clubUserRepositorySupport.findByClubSeq(clubSeq)
        return ClubUserDto( clubUsers[0].club, clubUsers.map{ e -> e.user}.toList() )
    }

    // 유저 컨트롤러에서 호출(가입한 클럽 리스트)
    fun getClubByUser(user: User): UserClubDto? {
        val clubUsers = clubUserRepositorySupport.findByUserSeq(user)
        return UserClubDto( clubUsers[0].user, clubUsers.map{ e -> e.club}.toList() )
    }

    fun retrieveClubs(offset: Long, page: Long, keyword: String): List<Club>? {
        return clubRepositorySupport.findByKeyword(offset, page, keyword)
    }

    /**
     * 새로운 모임을 생성한다.
     * 1. 모임 생성
     * 2. 생성한 유저가 해당 모임에 들어감
     * 3. 생성한 유저에게 모임장 권한을 줌 (Todo)
     */
    fun addClub(club:Club, superUser: User) {
        clubRepository.save(club)   // 1. 모임 생성
        val superUserClub = ClubUser(club, superUser)   // 2. 생성한 유저가 해당 모임에 들어감
        clubUserRepository.save(superUserClub)

        // TODO:: 3. 생성한 유저에게 모임장 권한을 줌
    }
}