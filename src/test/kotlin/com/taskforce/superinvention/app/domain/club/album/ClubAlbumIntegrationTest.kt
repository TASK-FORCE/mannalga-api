package com.taskforce.superinvention.app.domain.club.album

import com.taskforce.superinvention.app.domain.common.FileService
import com.taskforce.superinvention.app.domain.user.UserRepository
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumRegisterDto
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import com.taskforce.superinvention.config.test.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile

class ClubAlbumIntegrationTest: IntegrationTest() {

    @Autowired
    lateinit var sut: ClubAlbumService

    @Autowired
    lateinit var fileService: FileService

    @Autowired
    lateinit var userRepository: UserRepository

    @Test
    fun `모임 사진첩 사진 등록`() {

        // given
//        val image = MockMultipartFile("files", "test_image.png", "image/png", getResourceAsStream("/test-image-file.png"))
        val image = MockMultipartFile("files", "test_image.png", "image/png", getResourceAsStream("/large-size.png"))
//        val image = MockMultipartFile("files", "test.jpg", "image/jpg", getResourceAsStream("/test.jpg"))
//        val image = MockMultipartFile("files", "test.jpg", "image/jpg", getResourceAsStream("/test-large.jpg"))
        val s3path: S3Path = fileService.fileTempSave(image)
        val user = userRepository.findByUserId("1451001649")!!

        val registerDto = ClubAlbumRegisterDto(
            title = "으어아억",
            image = s3path
        )

        sut.registerClubAlbum(user, 6179, registerDto)
    }
}