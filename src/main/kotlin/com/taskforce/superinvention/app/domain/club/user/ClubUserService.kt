package com.taskforce.superinvention.app.domain.club.user

import com.taskforce.superinvention.app.domain.role.Role
import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.ClubUserStatusDto
import com.taskforce.superinvention.common.exception.InvalidInputException
import com.taskforce.superinvention.common.exception.club.UserIsNotClubMemberException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClubUserService(
    private val clubUserRepository: ClubUserRepository
) {

    // 클럽에서의 유저 상태 표시
    @Transactional
    fun getClubUserDetails(user: User?, clubSeq: Long): ClubUserStatusDto? {
        if(user == null) {
            return null
        }

        val clubUser = clubUserRepository.findClubUserWithRole(clubSeq, user)

        return clubUser?.let ( ::ClubUserStatusDto )
    }

    fun getValidClubUser(clubSeq: Long, user: User): ClubUser {
        return clubUserRepository.findByClubSeqAndUser(clubSeq, user)
            ?: throw UserIsNotClubMemberException()
    }
}
