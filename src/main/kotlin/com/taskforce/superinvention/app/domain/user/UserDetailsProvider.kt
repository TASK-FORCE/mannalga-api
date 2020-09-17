package com.taskforce.superinvention.app.domain.user

import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserDetailsProvider(
        private val userRepository: UserRepository
): UserDetailsService {

    companion object {
        val LOG = LoggerFactory.getLogger(UserDetailsProvider::class.java)
    }

    @Transactional
    override fun loadUserByUsername(userId: String): UserDetails {
        val user: User = userRepository.findByUserId(userId)!!

        return  org.springframework.security.core.userdetails.User
                .withUsername(user.userId)
                .password("")
                .authorities(user.userRoles)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(false)
                .build()
    }
}