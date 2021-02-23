package com.taskforce.superinvention.app.domain.common.image

import com.taskforce.superinvention.app.domain.common.image.resize.strategy.ImageResizeStrategyLocator
import com.taskforce.superinvention.app.web.dto.common.image.ResizeDto
import com.taskforce.superinvention.common.exception.InvalidInputException
import com.taskforce.superinvention.common.util.aws.s3.AwsS3Mo
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import com.taskforce.superinvention.common.util.file.FileMo
import org.apache.commons.io.FilenameUtils
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Service
class ImageService(
    private val awsS3Mo: AwsS3Mo,
    private val imageResizeStrategyLocator: ImageResizeStrategyLocator
) {
    companion object {
        const val TEMP_IMG_DIR_PATH = "temp/img"
        val invalidInputException = InvalidInputException()
    }

    fun fileImageSave(multipartFile: MultipartFile, resize: ResizeDto = ResizeDto()): S3Path {
        val file = FileMo.convertMultiPartToFile(multipartFile)

        if(resize.width != null && resize.height != null) {
            return awsS3Mo.uploadFileWithUUID(resizeImage(file, resize), TEMP_IMG_DIR_PATH)
        }

        return awsS3Mo.uploadFileWithUUID(file, TEMP_IMG_DIR_PATH)
    }

    private fun resizeImage(file: File, resize: ResizeDto): File {
        val extension = FilenameUtils.getExtension(file.name)

        val format: ImageFormat = ImageFormat.extensionOf(extension)
            ?: throw invalidInputException

        return imageResizeStrategyLocator.getStrategy(format)
            .resize(file, resize)
            .absoluteFile
    }

}