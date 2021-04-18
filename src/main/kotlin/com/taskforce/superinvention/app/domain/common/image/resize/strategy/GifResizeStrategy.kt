package com.taskforce.superinvention.app.domain.common.image.resize.strategy

import com.drew.imaging.ImageMetadataReader
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.nio.GifWriter
import com.taskforce.superinvention.app.domain.common.image.ImageFormat
import com.taskforce.superinvention.app.web.dto.common.image.ResizeDto
import com.taskforce.superinvention.common.util.file.image.gif.GifMo
import org.springframework.stereotype.Service
import java.io.File

@Service
class GifResizeStrategy: ImageResizeStrategy {

    override fun identify(imageFormat: ImageFormat): Boolean {
        return imageFormat == ImageFormat.GIF
    }

    override fun resize(file: File, resize: ResizeDto): File {
        val metadata = ImageMetadataReader.readMetadata(file)
        return if(GifMo.isAnimated(metadata)) {
            file
        } else {
             ImmutableImage
                .loader()
                .fromFile(file)
                .scaleTo(resize.width!!.toInt(), resize.height!!.toInt())
                .output(GifWriter.Default, File.createTempFile("s3-img", file.name))
        }
    }
}
