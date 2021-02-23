package com.taskforce.superinvention.app.domain.common.image.resize.strategy

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.GifWriter
import com.sksamuel.scrimage.nio.PngWriter
import com.taskforce.superinvention.app.domain.common.image.ImageFormat
import com.taskforce.superinvention.app.web.dto.common.image.ResizeDto
import org.springframework.stereotype.Service
import java.io.File

@Service
class PngResizeStrategy: ImageResizeStrategy {

    override fun identify(imageFormat: ImageFormat): Boolean {
        return imageFormat == ImageFormat.PNG
    }

    override fun resize(file: File, resize: ResizeDto): File {
        return ImmutableImage
            .loader()
            .fromFile(file)
            .scaleTo(resize.width!!.toInt(), resize.height!!.toInt())
            .output(PngWriter.NoCompression, File.createTempFile("s3-img", file.name))
    }
}