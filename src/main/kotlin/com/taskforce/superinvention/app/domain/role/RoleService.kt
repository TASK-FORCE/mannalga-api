package com.taskforce.superinvention.app.domain.role

import com.taskforce.superinvention.app.domain.club.Club
import com.taskforce.superinvention.app.domain.user.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoleService(
        val roleRepository: RoleRepository,
        val roleGroupRepository: RoleGroupRepository,
        val clubUserRoleRepository: ClubUserRoleRepository
) {
    /**
     * 모임원의 권한중 매니저 이상의 권한이 있는지 확인한다.
     * 
     * @author eric
     * @return 매니저, 마스터 권한중 하나라도 있으면 true
     */
    fun hasClubManagerAuth(club: Club, user: User): Boolean {
        val clubUserRoles = getClubUserRoles(club, user)
        val managerAuth = setOf(Role.RoleName.MASTER, Role.RoleName.MANAGER)
        return clubUserRoles.stream().map { clubUserRole -> clubUserRole.role.name }.anyMatch{roleName -> managerAuth.contains(roleName)}
    }

    fun findByRoleName(roleName: Role.RoleName) :Role = roleRepository.findByName(roleName)

    /**
     * 모임원이 가지고 있는 모든 권한을 조회한다
     */
    fun getClubUserRoles(club: Club, user: User): Set<ClubUserRole> {
        val clubUserRoles: Set<ClubUserRole> = clubUserRoleRepository.findByClubAndUser(club, user)
        return clubUserRoles
    }
}