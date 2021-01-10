package com.taskforce.superinvention.document.common

import com.taskforce.superinvention.common.advice.ErrorResponse
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import com.taskforce.superinvention.config.MockitoHelper
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.commonResponseField
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.documentation.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.test.ApiDocumentationTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
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
import java.io.FileInputStream

class CommonDocumentation: ApiDocumentationTest() {

    lateinit var multipartFile: MockMultipartFile

    @BeforeEach
    fun setup() {
        val inputFile = FileInputStream("${getResourcePath()}/test-image-file.png")
        multipartFile = MockMultipartFile("file", "test-image-file.png", "multipart/form-data", inputFile)
    }

    @Test
    fun `파일 임시저장`() {
        // given
        given(fileService.fileTempSave(MockitoHelper.anyObject())).willReturn(
                S3Path(
                        absolutePath = "http://{aws-s3-domain}/{file-path}/{file-name}",
                        filePath = "{file-path}/{file-name}",
                        fileName = "{file-name}"
                )
        )

        val image = MockMultipartFile(
                "file",
                "image.png",
                "image/png",
                "<<png data>>".toByteArray()
        )

        // when
        val result = mockMvc.perform(
                multipart("/common/temp/file").file(image)
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

    @Test
    fun `공통 에러 포맷`() {
        // when
        val result = mockMvc.perform(
                get("/users/profile")
        ).andDo(print())

        // then
        result.andExpect(status().isInternalServerError)
                .andDo(
                        document("error", getDocumentRequest(), getDocumentResponse(),
                                responseFields(
                                        fieldWithPath("message").type(JsonFieldType.STRING).description("에러 발생 사유에 대한 메세지, 사용자 전달용"),
                                        fieldWithPath("stackTrace").type(JsonFieldType.STRING).description("에러에 대한 스택 트레이스 정보 (디버깅용)")
                                )
                        )
                )
    }
}