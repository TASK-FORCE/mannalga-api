package com.taskforce.superinvention.app.domain.common

import com.taskforce.superinvention.common.util.aws.s3.AwsS3Mo
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import org.springframework.stereotype.Service

@Service
class CommonService (
        val awsS3Mo: AwsS3Mo
){
//    fun tempSaveFile(): S3Path {
//
//        awsS3Mo.uploadFile()
//        return null
//    }
}