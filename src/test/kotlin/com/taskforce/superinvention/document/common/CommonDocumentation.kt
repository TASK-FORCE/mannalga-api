package com.taskforce.superinvention.document.common

import com.ninjasquad.springmockk.MockkBean
import com.taskforce.superinvention.app.domain.common.FileService
import com.taskforce.superinvention.app.domain.common.image.ImageService
import com.taskforce.superinvention.app.domain.user.UserService
import com.taskforce.superinvention.app.web.controller.CommonController
import com.taskforce.superinvention.app.web.controller.user.UserController
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonResponseField
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.test.ApiDocumentationTestV2
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.partWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParts
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(CommonController::class)
class CommonDocumentation: ApiDocumentationTestV2() {

    lateinit var multipartFile: MockMultipartFile

    @MockkBean
    lateinit var imageService: ImageService

    @MockkBean
    lateinit var fileService: FileService

    @BeforeEach
    fun setup() {
        val inputFile = getResourceAsStream("/img/test.jpg")
        multipartFile = MockMultipartFile("file", "test.jpg", "multipart/form-data", inputFile)
    }

    @Test
    fun `파일 임시저장`() {
        // given
        every { imageService.fileImageSave(any()) } returns
                S3Path(
                        absolutePath = "http://{aws-s3-domain}/{file-path}/{file-name}",
                        filePath = "{file-path}/{file-name}",
                        fileName = "{file-name}"
                )

        val image = MockMultipartFile(
                "file",
                "image.png",
                "image/png",
                "<<png data>>".toByteArray()
        )

        // when
        val result = mockMvc.perform(
                multipart("/common/temp/image").file(image)
        ).andDo(print())

        // then
        result.andExpect(status().isOk)
                .andDo(
                        document("temp-file-upload", getDocumentRequest(), getDocumentResponse(),
                                requestParts(
                                        partWithName("file").description("임시 저장 단일파일")
                                ),
                                responseFields(
                                        *commonResponseField(),
                                        fieldWithPath("data.absolutePath").type(JsonFieldType.STRING).description("임시저장 파일 절대 경로"),
                                        fieldWithPath("data.filePath").type(JsonFieldType.STRING).description("도메인 제외 경로"),
                                        fieldWithPath("data.fileName").type(JsonFieldType.STRING).description("파일명")
                                )
                        )
                )
    }
}