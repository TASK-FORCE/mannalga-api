package com.taskforce.superinvention.app.domain.club.user

import com.taskforce.superinvention.app.domain.user.User
import com.taskforce.superinvention.app.web.dto.club.ClubUserStatusDto
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

        val clubUser = clubUserRepository.findClubUserWithRole(clubSeq, user) ?: return null
        val roleNames= clubUser.clubUserRoles.map { clubUserRoles -> clubUserRoles.role.name }

        return ClubUserStatusDto(roleNames, clubUser.isLiked ?: false )
    }
}