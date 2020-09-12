package com.taskforce.superinvention.document.common

import com.taskforce.superinvention.common.util.aws.s3.S3Path
import com.taskforce.superinvention.config.ApiDocumentUtil.getDocumentRequest
import com.taskforce.superinvention.config.ApiDocumentUtil.getDocumentResponse
import com.taskforce.superinvention.config.ApiDocumentationTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.*
import org.springframework.restdocs.request.RequestDocumentation.partWithName
import org.springframework.restdocs.request.RequestDocumentation.requestParts
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
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
        given(fileService.fileTempSave(multipartFile)).willReturn(
            S3Path(
                absolutePath = "http://{aws-s3-domain}/{file-path}/{file-name}",
                filePath = "{file-path}/{file-name}",
                fileName = "{file-name}"
            )
        )


        // when
        val result = mockMvc.perform(
                multipart("/common/temp/file")
                        .file(multipartFile)
        ).andDo(print())

        // then
        val andDo: ResultActions = result.andExpect(status().isOk)
                .andDo(
                        document("temp-file-upload", getDocumentRequest(), getDocumentResponse(),
                                requestParts(
                                        partWithName("file").description("임시 저장 단일파일")
                                ),
                                responseFields(
                                        fieldWithPath("absolutePath").type(JsonFieldType.STRING).description("임시저장 파일 절대 경로"),
                                        fieldWithPath("filePath").type(JsonFieldType.STRING).description("도메인 제외 경로"),
                                        fieldWithPath("fileName").type(JsonFieldType.STRING).description("파일명")
                                )
                        )
                )
    }
}