package com.taskforce.superinvention.app.domain.common.image.webp.convert

import com.taskforce.superinvention.app.domain.common.image.ImageFormat
import com.taskforce.superinvention.app.domain.common.image.webp.convert.strategy.WebpConvertStrategyLocator
import com.taskforce.superinvention.common.exception.InvalidInputException
import com.taskforce.superinvention.common.util.aws.s3.AwsS3Mo
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import java.io.File

@Service
class WebpConvertService(
    private val strategyLocator: WebpConvertStrategyLocator,
    private val awsS3Mo: AwsS3Mo
) {

    companion object {
        val invalidInputException = InvalidInputException()
    }

    // s3 상의 이미지 경로에 /이미지.webp 형태의 압축본 생성
    fun convertToWebP(s3Path: S3Path): S3Path {
        val folderPath = s3Path.folderPath()

        val fileName  = FilenameUtils.removeExtension(s3Path.fileName)
        val extension = FilenameUtils.getExtension(s3Path.fileName)
        val s3File    = awsS3Mo.getObjectAsFile(s3Path)

        val format: ImageFormat = ImageFormat.extensionOf(extension)
            ?: throw invalidInputException

        val convertStrategy = strategyLocator.getStrategy(format)
        val convertedFile: File = convertStrategy.convert(fileName, s3File, WebpCompressionParam())

        val convertedExtension = ImageFormat.extensionOf(FilenameUtils.getExtension(convertedFile.name))

        // 변환에 실패한 경우 원본 url을 리턴
        if(convertedExtension != ImageFormat.WEBP) {
            return s3Path
        }
        
        return awsS3Mo.uploadFile(convertedFile, folderPath)
    }
}
