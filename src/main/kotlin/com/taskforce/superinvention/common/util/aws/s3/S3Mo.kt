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
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.annotation.PostConstruct

@Component
class S3Mo (
        @Value("\${aws.s3.bucketName}")
        private val bucketName: String? = null,

        @Value("\${aws.s3.endpointUrl}")
        private val endpointUrl: String? = null,

        @Value("\${aws.s3.accessKey}")
        private val accessKey: String? = null,

        @Value("\${aws.s3.secretAccessKey}")
        private val secretKey: String?  = null
){
    lateinit var s3client: AmazonS3

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
    fun uploadFile(multipartFile: MultipartFile, dirPath: String): S3Path {
        val file = convertMultiPartToFile(multipartFile)
        val fileName= generateFileName(multipartFile)
        val filePath     = "$dirPath/$fileName"
        val absolutePath = "$endpointUrl/$bucketName/$filePath"

        uploadFileToS3bucket(filePath, file)
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
        deleteFileFromS3Bucket(s3PathFrom.bucketPath!!)

        return S3Path(
                absolutePath = endpointUrl + filePath,
                bucketPath = filePath,
                fileName   = s3PathFrom.fileName
        )
    }

    // AWS S3 파일 삭제 함수
    fun deleteFileFromS3Bucket(bucketPath: String) {
        s3client.deleteObject(DeleteObjectRequest(bucketName, bucketPath))
    }

    private fun convertMultiPartToFile(file: MultipartFile): File {
        val convFile = File(file.originalFilename)
        val fos = FileOutputStream(convFile)
        fos.write(file.bytes)
        fos.close()
        return convFile
    }

    private fun generateFileName(multiPart: MultipartFile): String {
        return String.format("%s_%s", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss_SSSSS")),
                multiPart.originalFilename!!.replace(" ".toRegex(), "_"))
    }

    private fun uploadFileToS3bucket(filePath: String, file: File) {
        s3client.putObject(PutObjectRequest(bucketName, filePath, file)
                .withCannedAcl(CannedAccessControlList.PublicRead))
    }

    private fun uploadFileToS3bucket(filePath: String, file: File, date: Date) {
        s3client.putObject(PutObjectRequest(bucketName, filePath, file)
                .withCannedAcl(CannedAccessControlList.PublicRead))
                .expirationTime=date
    }
}