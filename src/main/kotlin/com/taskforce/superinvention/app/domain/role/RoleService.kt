package com.taskforce.superinvention.app.domain.role

import com.taskforce.superinvention.app.domain.club.user.ClubUser
import com.taskforce.superinvention.app.domain.club.user.ClubUserRepository
import com.taskforce.superinvention.app.domain.user.User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.SQLException

@Service
class RoleService(
        private val roleRepository: RoleRepository,
        private val roleGroupRepository: RoleGroupRepository,
        private val clubUserRoleRepository: ClubUserRoleRepository,
        private val clubUserRepository: ClubUserRepository
) {
    /**
     * 모임원의 권한중 매니저 이상의 권한이 있는지 확인한다.
     * 
     * @author eric
     * @return 매니저, 마스터 권한중 하나라도 있으면 true
     */
    @Transactional
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

    fun findByRoleNameIn(roleNameList: Set<Role.RoleName>): Set<Role> {
        return roleRepository.findByNameIn(roleNameList)
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

    @Transactional
    fun hasClubMemberAuth(clubSeq: Long, user: User): Boolean {
        val clubUser = clubUserRepository.findByClubSeqAndUserSeq(clubSeq, user.seq!!) ?: return false
        val clubUserRole = getClubUserRoles(clubUser)
        val memberAuth = setOf(Role.RoleName.MASTER, Role.RoleName.MANAGER, Role.RoleName.CLUB_MEMBER)

        return clubUserRole.map { clubUserRole -> clubUserRole.role.name }
            .any { roleName -> memberAuth.contains(roleName) }
    }

    @Transactional
    fun hasClubMemberAuth(clubUser: ClubUser): Boolean {
        val clubUserRole = getClubUserRoles(clubUser)
        val memberAuth = setOf(Role.RoleName.MASTER, Role.RoleName.MANAGER, Role.RoleName.CLUB_MEMBER)

        return clubUserRole.map { clubUserRole -> clubUserRole.role.name }
            .any { roleName -> memberAuth.contains(roleName) }
    }

    @Transactional
    fun withdrawRole(withdrawClubUser: ClubUser) {
        clubUserRoleRepository.deleteAll(withdrawClubUser.clubUserRoles)
        val memberRole = roleRepository.findByName(Role.RoleName.MEMBER)
        val memberUserRole = ClubUserRole(withdrawClubUser, memberRole)
        clubUserRoleRepository.save(memberUserRole)
    }

    @Transactional
    fun changeClubMaster(clubSeq: Long, targetClubUser: ClubUser) {
        // MASTER 권한 제거
        val clubMaster = clubUserRepository.findMasterByClubSeq(clubSeq)
        changeClubUserRoles(clubMaster, setOf(roleRepository.findByName(Role.RoleName.CLUB_MEMBER)))

        // MASTER 권한 생성
        changeClubUserRoles(targetClubUser, setOf(roleRepository.findByName(Role.RoleName.MASTER)))
    }
}