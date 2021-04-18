package com.taskforce.superinvention.app.domain.common.image.resize

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.ImmutableImageLoader
import com.taskforce.superinvention.app.domain.common.image.ImageService
import com.taskforce.superinvention.app.domain.common.image.resize.strategy.GifResizeStrategy
import com.taskforce.superinvention.app.domain.common.image.resize.strategy.ImageResizeStrategyLocator
import com.taskforce.superinvention.app.domain.common.image.resize.strategy.JpgResizeStrategy
import com.taskforce.superinvention.app.domain.common.image.resize.strategy.PngResizeStrategy
import com.taskforce.superinvention.app.web.dto.common.image.ResizeDto
import com.taskforce.superinvention.common.util.aws.s3.AwsS3Mo
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import com.taskforce.superinvention.common.util.file.FileMo
import com.taskforce.superinvention.config.test.MockTest
import io.mockk.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockMultipartFile
import java.io.File

class ImageResizeServiceTest: MockTest()  {

    private lateinit var imageService              : ImageService
    private lateinit var awsS3Mo                   : AwsS3Mo
    private lateinit var imageResizeStrategyLocator: ImageResizeStrategyLocator

    @BeforeEach
    fun setup() {
        awsS3Mo                    = mockk()
        imageResizeStrategyLocator = ImageResizeStrategyLocator(
            listOf(JpgResizeStrategy(), GifResizeStrategy(), PngResizeStrategy())
        )

        imageService = spyk(
            ImageService(
            awsS3Mo,
            imageResizeStrategyLocator
        ), recordPrivateCalls = true)
    }

    @Test
    fun `이미지 높이와 폭을 받은 경우, 이미지를 리사이징한 형태로 AWS S3에 업로드한다` () {

        // given
        val mockMultipartFile = MockMultipartFile("file", "test.jpg", "multipart/form-data", getResourceAsStream("/img/test.jpg"))
        val resizeDto = ResizeDto(
            width  = 100,
            height = 100
        )

        every { awsS3Mo.uploadFileWithUUID(any<File>(), any()) } returns S3Path()

        // when
        imageService.fileImageSave(mockMultipartFile, resizeDto)

        verify(exactly = 1) { imageService.resizeImage(any(), resizeDto) }
    }

    @Test
    fun `이미지 높이와 폭을 모두 받지 않으면, 원본 이미지를 AWS S3에 업로드한다` () {

        // given
        val mockMultipartFile = MockMultipartFile("file", "test.jpg", "multipart/form-data", getResourceAsStream("/img/test.jpg"))
        val resizeDto = ResizeDto(
            width  = 100,
            height = null
        )

        every { awsS3Mo.uploadFileWithUUID(any<File>(), any()) } returns S3Path()

        // when
        imageService.fileImageSave(mockMultipartFile, resizeDto)

        verify(exactly = 0) { imageService.resizeImage(any(), resizeDto)}
    }

    @Test
    fun `애니메이션 GIF의 경우는 높이와 폭 설정이 있더라도 리사이징을 하지 않는다` () {

        // given
        val mockMultipartFile = MockMultipartFile("file", "animated.gif", "multipart/form-data", getResourceAsStream("/img/animated.gif"))
        val file = FileMo.convertMultiPartToFile(mockMultipartFile)

        val originImg = ImmutableImage.loader().fromFile(file)

        val resizeDto = ResizeDto(
            width  = 100,
            height = 101
        )

        // when
        val resizedImage = ImmutableImage.loader().fromFile(imageService.resizeImage(file, resizeDto))

        // then
        assertEquals(originImg.width , resizedImage.width)
        assertEquals(originImg.height, resizedImage.height)
    }
}
