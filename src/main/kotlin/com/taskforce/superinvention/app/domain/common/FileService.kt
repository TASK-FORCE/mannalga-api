package com.taskforce.superinvention.app.domain.common

import com.taskforce.superinvention.common.util.aws.s3.AwsS3Mo
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class FileService (
        private val awsS3Mo: AwsS3Mo
){
    companion object {
        const val TEMP_IMG_DIR_PATH = "temp/img"
    }

    fun fileTempSave(file: MultipartFile): S3Path {
        return awsS3Mo.uploadFile(file, TEMP_IMG_DIR_PATH)
    }
}