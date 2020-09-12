package com.taskforce.superinvention.common.util.aws.s3

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.*
import com.taskforce.superinvention.common.util.file.FileMo
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
){
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
    fun uploadFile(multipartFile: MultipartFile, s3DirPath: String): S3Path {
        val file      = FileMo.convertMultiPartToFile(multipartFile)
        val fileName= FileMo.generateUUID(multipartFile)
        val filePath       = "$s3DirPath/$fileName"
        val absolutePath   = "$endpointUrl/$bucketName/$filePath"

        uploadFileToBucket(filePath, file)
        file.delete()

        return S3Path(
                absolutePath = absolutePath,
                filePath = filePath,
                fileName = fileName
        )
    }

     // AWS S3 동일 버킷 내 파일 이동
    fun moveFile(s3PathFrom: S3Path, s3PathTo: String?): S3Path {
        val copyObjRequest: CopyObjectRequest = CopyObjectRequest(bucketName, s3PathFrom.filePath, bucketName, s3PathTo)
                                                .withCannedAccessControlList(CannedAccessControlList.PublicRead)
        s3client.copyObject(copyObjRequest)

        val filePath = s3client.getUrl(bucketName, s3PathTo).path
        deleteFile(s3PathFrom.filePath!!)

        return S3Path(
                absolutePath = endpointUrl + filePath,
                filePath = filePath,
                fileName = s3PathFrom.fileName
        )
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
}