package com.taskforce.superinvention.app.domain.user

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import javax.transaction.Transactional

@SpringBootTest
class UserServiceTest {
    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    @Transactional
    fun findById() {
        var userRoles: Set<UserRole> = HashSet<UserRole>();
        var user: User = User("eric.cc", "에릭", "잘생기고 귀여운 에릭", UserType.KAKAO, userRoles)
        val savedUser = userRepository.save(user)
        val findUser = userService.getUserById("eric.cc")
        assertEquals(findUser, savedUser);
    }

}