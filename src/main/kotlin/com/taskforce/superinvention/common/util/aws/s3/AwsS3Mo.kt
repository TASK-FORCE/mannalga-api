package com.taskforce.superinvention.common.util.aws.s3

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.*
import com.taskforce.superinvention.common.util.file.FileMo
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import javax.annotation.PostConstruct


@Component
class AwsS3Mo(
        @Value("\${aws.s3.bucketName}")
        private val bucketName: String? = null,

        @Value("\${aws.s3.endpointUrl}")
        private val endpointUrl: String? = null,

        @Value("\${aws.s3.accessKey}")
        private val accessKey: String? = null,

        @Value("\${aws.s3.secretAccessKey}")
        private val secretKey: String? = null
) {
    private lateinit var s3client: AmazonS3

    @PostConstruct
    private fun initAmazonS3() {
        val credentials: AWSCredentials = BasicAWSCredentials(accessKey, secretKey)
        s3client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(AWSStaticCredentialsProvider(credentials))
                .withRegion(Regions.AP_NORTHEAST_2)
                .build()
    }

    // AWS S3 파일 업로드
    fun uploadFileWithUUID(multipartFile: MultipartFile, s3DirPath: String): S3Path {
        val file: File = FileMo.convertMultiPartToFile(multipartFile)
        return uploadFileWithUUID(file, s3DirPath)
    }

    fun uploadFileWithUUID(file: File, s3DirPath: String): S3Path {
        val fileName       =  FileMo.generateUUID(file)
        val filePath       = "$s3DirPath/$fileName"
        val absolutePath   = "$endpointUrl/$filePath"
        uploadFileToBucket(filePath, file)
        file.delete()

        return S3Path(
            absolutePath = absolutePath,
            filePath = filePath,
            fileName = fileName
        )
    }

    fun uploadFile(file: File, s3DirPath: String): S3Path {
        val fileName       =  file.name
        val filePath       = "$s3DirPath/$fileName"
        val absolutePath   = "$endpointUrl/$filePath"
        uploadFileToBucket(filePath, file)
        file.delete()

        return S3Path(
            absolutePath = absolutePath,
            filePath = filePath,
            fileName = fileName
        )
    }
    // AWS S3 동일 버킷 내 파일 이동
    fun moveFile(s3PathFrom: S3Path, s3PathTo: String): S3Path {
        val copyObjRequest: CopyObjectRequest = CopyObjectRequest(bucketName, s3PathFrom.filePath, bucketName, s3PathTo)
                                                .withCannedAccessControlList(CannedAccessControlList.PublicRead)
        s3client.copyObject(copyObjRequest)
        deleteFile(s3PathFrom.filePath)

        return S3Path(
                absolutePath = "${endpointUrl}/${s3PathTo}",
                filePath = s3PathTo,
                fileName = s3PathFrom.fileName
        )
    }

    fun getObjectAsFile(imgS3Path: S3Path): File {
        val s3Object = getObject(imgS3Path)

        val extension = FilenameUtils.getExtension(s3Object.key)
        val tempFile  = File.createTempFile("temp_aws_s3", extension)

        val s3ObjectByteArr = s3Object.objectContent.readAllBytes()
        FileUtils.writeByteArrayToFile(tempFile, s3ObjectByteArr)

        return tempFile
    }

    fun getObjectAsByteArr(imgS3Path: S3Path): ByteArray {
        val s3Object = getObject(imgS3Path)

        return s3Object.objectContent.readAllBytes()
    }

    // AWS S3 파일 삭제 함수
    fun deleteFile(bucketPath: String) {
        s3client.deleteObject(DeleteObjectRequest(bucketName, bucketPath))
    }

    private fun uploadFileToBucket(filePath: String, file: File) {
        val pubObjReq = PutObjectRequest(bucketName, filePath, file)
                .withCannedAcl(CannedAccessControlList.PublicRead)

        s3client.putObject(pubObjReq)
    }

    private fun getObject(imgS3Path: S3Path): S3Object {
        val copyObjRequest = GetObjectRequest(bucketName, imgS3Path.filePath)
        val s3Object: S3Object = s3client.getObject(copyObjRequest)
        return s3Object
    }
}