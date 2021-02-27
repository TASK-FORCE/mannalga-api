package com.taskforce.superinvention.app.domain.common.image.resize.strategy

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.JpegWriter
import com.taskforce.superinvention.app.domain.common.image.ImageFormat
import com.taskforce.superinvention.app.web.dto.common.image.ResizeDto
import org.springframework.stereotype.Service
import java.io.File

@Service
class JpgResizeStrategy: ImageResizeStrategy {

    override fun identify(imageFormat: ImageFormat): Boolean {
        return imageFormat == ImageFormat.JPEG ||
               imageFormat == ImageFormat.JPG
    }

    override fun resize(file: File, resize: ResizeDto): File {
        return ImmutableImage
            .loader()
            .fromFile(file)
            .scaleTo(resize.width!!.toInt(), resize.height!!.toInt())
            .output(JpegWriter.Default, File.createTempFile("s3-img", file.name))
    }
}