package com.taskforce.superinvention.app.domain.user

import org.springframework.stereotype.Service

@Service
class UserService(
        var userRepository: UserRepository,
        var userRepositorySupport: UserRepositorySupport
) {

    fun getUserById(id: String): User {
        return userRepositorySupport.findById(id)
    }

}