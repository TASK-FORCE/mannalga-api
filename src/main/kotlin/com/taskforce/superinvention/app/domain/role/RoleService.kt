package com.taskforce.superinvention.app.domain.role

import com.taskforce.superinvention.app.domain.club.user.ClubUser
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
    fun hasClubManagerAuth(clubUser: ClubUser): Boolean {

        val clubUserRoles = getClubUserRoles(clubUser)
        val managerAuth = setOf(Role.RoleName.MASTER, Role.RoleName.MANAGER)
        return clubUserRoles
                .map { clubUserRole -> clubUserRole.role.name }
                .any { roleName -> managerAuth.contains(roleName) }
    }
    
    fun findByRoleName(roleName: Role.RoleName): Role = roleRepository.findByName(roleName)

    /**
     * 모임원이 가지고 있는 모든 권한을 조회한다
     */
    fun getClubUserRoles(clubUser: ClubUser): Set<ClubUserRole> {
        val clubUserRoles: Set<ClubUserRole> = clubUserRoleRepository.findByClubUser(clubUser)
        return clubUserRoles
    }

    fun findBySeqList(roleSeqList: Set<Long>): Set<Role> {
        return roleRepository.findBySeqIn(roleSeqList);
    }

    fun hasClubMasterAuth(clubUser: ClubUser): Boolean {
        return getClubUserRoles(clubUser).any { clubUserRole -> Role.RoleName.MASTER == clubUserRole.role.name }
    }

    @Transactional
    fun changeClubUserRoles(clubUser: ClubUser, roles: Set<Role>) {
        // 기존 권한 삭제
        val findByClubUser = clubUserRoleRepository.findByClubUser(clubUser)
        clubUserRoleRepository.deleteAll(findByClubUser)

        // 새로운 권한 적용
        val clubUserRoles = roles.map { role ->
            ClubUserRole(
                    clubUser = clubUser,
                    role = role
            )
        }
        clubUserRoleRepository.saveAll(clubUserRoles)
    }
}