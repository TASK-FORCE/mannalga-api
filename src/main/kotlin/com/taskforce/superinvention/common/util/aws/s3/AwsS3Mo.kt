package com.taskforce.superinvention.common.util.aws.s3

import com.amazonaws.auth.AWSCredentials
import com.amazonaws.auth.AWSStaticCredentialsProvider
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.AmazonS3ClientBuilder
import com.amazonaws.services.s3.model.CannedAccessControlList
import com.amazonaws.services.s3.model.CopyObjectRequest
import com.amazonaws.services.s3.model.DeleteObjectRequest
import com.amazonaws.services.s3.model.PutObjectRequest
import com.taskforce.superinvention.common.util.file.FileMo
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*
import javax.annotation.PostConstruct

@Component
class AwsS3Mo (
        @Value("\${aws.s3.bucketName}")
        private val bucketName: String? = null,

        @Value("\${aws.s3.endpointUrl}")
        private val endpointUrl: String? = null,

        @Value("\${aws.s3.accessKey}")
        private val accessKey: String? = null,

        @Value("\${aws.s3.secretAccessKey}")
        private val secretKey: String?  = null
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
    fun uploadFile(multipartFile: MultipartFile, dirPath: String, day: Int = 0): S3Path {
        val file      = FileMo.convertMultiPartToFile(multipartFile)
        val fileName= FileMo.generateUUID(multipartFile)
        val filePath       = "$dirPath/$fileName"
        val absolutePath   = "$endpointUrl/$bucketName/$filePath"

        // @Todo TTL 쓰기

        /**
         * val now = LocalDateTime.now().atZone(ZoneId.of(TIME_ZONE_KST))
         * val issuedDate  = Date.from(now.toInstant())
         * val expiredDate = Date.from(now.plusDays(expireDay).toInstant() )
         */
        uploadFileToBucket(filePath, file)
        file.delete()

        return S3Path(
                absolutePath= absolutePath,
                bucketPath= filePath,
                fileName  = fileName
        )
    }

     // AWS S3 동일 버킷 내 파일 이동
    fun moveFile(s3PathFrom: S3Path, s3PathTo: String?): S3Path {
        val copyObjRequest: CopyObjectRequest = CopyObjectRequest(bucketName, s3PathFrom.bucketPath, bucketName, s3PathTo)
                                                .withCannedAccessControlList(CannedAccessControlList.PublicRead)
        s3client.copyObject(copyObjRequest)

        val filePath = s3client.getUrl(bucketName, s3PathTo).path
        deleteFile(s3PathFrom.bucketPath!!)

        return S3Path(
                absolutePath = endpointUrl + filePath,
                bucketPath = filePath,
                fileName   = s3PathFrom.fileName
        )
    }

    // AWS S3 파일 삭제 함수
    fun deleteFile(bucketPath: String) {
        s3client.deleteObject(DeleteObjectRequest(bucketName, bucketPath))
    }

    private fun uploadFileToBucket(filePath: String, file: File) {
        val putObject = s3client.putObject(PutObjectRequest(bucketName, filePath, file)
                                                .withCannedAcl(CannedAccessControlList.PublicRead))
        putObject.expirationTime
    }

    private fun uploadFile(filePath: String, file: File, date: Date) {
        s3client.putObject(PutObjectRequest(bucketName, filePath, file)
                .withCannedAcl(CannedAccessControlList.PublicRead))
                .expirationTime=date
    }
}