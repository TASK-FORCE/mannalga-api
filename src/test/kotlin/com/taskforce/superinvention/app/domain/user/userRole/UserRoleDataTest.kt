package com.taskforce.superinvention.app.domain.user.userRole

import com.taskforce.superinvention.config.DataJpaRepoTest
import org.springframework.beans.factory.annotation.Autowired


class UserRoleDataTest : DataJpaRepoTest() {

    @Autowired
    lateinit var userRoleRepository: UserRoleRepository
}