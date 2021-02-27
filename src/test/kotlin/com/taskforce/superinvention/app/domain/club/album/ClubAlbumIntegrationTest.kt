package com.taskforce.superinvention.app.domain.club.album

import com.taskforce.superinvention.app.domain.common.FileService
import com.taskforce.superinvention.app.domain.user.UserRepository
import com.taskforce.superinvention.app.web.dto.club.album.ClubAlbumRegisterDto
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import com.taskforce.superinvention.config.test.IntegrationTest
import org.junit.jupiter.api.Disabled
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

    @Disabled
    @Test
    fun `모임 사진첩 사진 등록 - 실제 API 호출함 주의`() {

        // given
        
        // 1. PNG 파일
        // val image = MockMultipartFile("files", "test_image.png", "image/png", getResourceAsStream("/img/large-size.png"))
        
        // 2. jpg 파일
        // val image = MockMultipartFile("files", "test.jpg", "image/jpg", getResourceAsStream("/img/large-size.jpg"))
        val image = MockMultipartFile("files", "test.jpg", "image/jpg", getResourceAsStream("/img/test.jpg"))
        // 3. gif 파일
        // val image = MockMultipartFile("files", "test.gif", "image/gif", getResourceAsStream("/img/big-animated.gif"))
        val s3path: S3Path = fileService.fileTempSave(image)
        val user = userRepository.findByUserId("1451001649")!!

        val registerDto = ClubAlbumRegisterDto(
            title = "으어아억",
            image = s3path
        )

        sut.registerClubAlbum(user, 6179, registerDto)
    }
}