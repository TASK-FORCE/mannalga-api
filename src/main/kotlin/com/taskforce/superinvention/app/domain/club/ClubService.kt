package com.taskforce.superinvention.app.domain.club

import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.ClubUserDto
import org.springframework.stereotype.Service

@Service
class ClubService(
        private var clubRepository: ClubRepository,
        private var clubRepositorySupport: ClubRepositorySupport,
        private var clubUserRepository: ClubUserRepository,
        private var clubUserRepositorySupport: ClubUserRepositorySupport
) {
    fun getClubBySeq(seq: Long): Club? {
        return clubRepositorySupport.findBySeq(seq)
    }

    fun getAllClubs(): List<Club>? {
        return clubRepository.findAll()
    }

    fun getClubUserList(clubSeq: Long): ClubUserDto {
        val clubUsers = clubUserRepositorySupport.findByClubSeq(clubSeq)
        return ClubUserDto( clubUsers[0].club, clubUsers.map{ e -> User(e.user)}.toList() )
    }

    fun retrieveClubs(keyword: String): List<Club>? {
        return clubRepositorySupport.findByKeyword(keyword)
    }
}