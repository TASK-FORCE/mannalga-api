package com.taskforce.superinvention.common.aws.s3

import com.taskforce.superinvention.common.util.aws.s3.AwsS3Mo
import com.taskforce.superinvention.config.test.IntegrationTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import java.io.FileInputStream

class AwsS3MoTest: IntegrationTest() {

    @Autowired
    lateinit var awsS3Mo: AwsS3Mo

    lateinit var multipartFile: MockMultipartFile

    @BeforeEach
    fun setup() {
        val inputFile = FileInputStream("${getResourcePath()}/test-image-file.png")
        multipartFile = MockMultipartFile("test-image-file", "test-image-file.png", "multipart/form-data", inputFile)
    }

    @Disabled @Test
    fun `AWS S3 파일 업로드`() {
        val s3Path = awsS3Mo.uploadFileWithUUID(multipartFile, "temp")
        print(s3Path)
    }
}