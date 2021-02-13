package com.taskforce.superinvention.app.domain.user

import com.taskforce.superinvention.app.domain.common.FileService
import com.taskforce.superinvention.app.web.dto.user.UserProfileUpdateDto
import com.taskforce.superinvention.config.test.IntegrationTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile

class UserServiceIntegrationTest: IntegrationTest() {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var fileService: FileService

    @Disabled
    @Test
    fun `유저 프로필 사진 변경`() {

        // given
        val image = MockMultipartFile("files", "test_image.png", "image/png", getResourceAsStream("/test-image-file.png"))
        val user = userRepository.findByUserId("1451001649")!!
        val fileTempSave = fileService.fileTempSave(image)

        val body = UserProfileUpdateDto(
            profileImage = fileTempSave
        )

        // when
        val updateUser = userService.updateUser(user, body)

        // then
        // 정상적으로 move 됫는지 확인
        Assertions.assertNotEquals(fileTempSave.absolutePath, updateUser.profileImageLink)
    }
}