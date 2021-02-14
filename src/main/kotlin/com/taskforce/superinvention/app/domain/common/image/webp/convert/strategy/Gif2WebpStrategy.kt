package com.taskforce.superinvention.app.domain.common.image.webp.convert.strategy

import com.drew.imaging.ImageMetadataReader
import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.webp.WebpWriter
import com.taskforce.superinvention.app.domain.common.image.ImageFormat
import com.taskforce.superinvention.app.domain.common.image.webp.convert.WebpCompressionParam
import com.taskforce.superinvention.app.domain.common.image.webp.convert.handler.WebpAnimatedWriter
import com.taskforce.superinvention.common.util.file.image.gif.GifMo
import org.apache.commons.io.FileUtils
import org.springframework.stereotype.Service
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileOutputStream

@Service
class Gif2WebpStrategy: WebpConvertStrategy {

    override fun identify(imageFormat: ImageFormat): Boolean {
        return imageFormat == ImageFormat.GIF
    }

    override fun convert(fileName: String, file: File, param: WebpCompressionParam): File {
        val metadata = ImageMetadataReader.readMetadata(file)

        val bytes = if(GifMo.isAnimated(metadata)) {
            WebpAnimatedWriter.DEFAULT.writeAsByteArray(file)
        } else {
            ImmutableImage.loader()
                .fromFile(file)
                .bytes(WebpWriter.DEFAULT)
        }

        val convertedFile = File("${fileName}.webp")
        FileUtils.writeByteArrayToFile(convertedFile, bytes)
        return convertedFile
    }
}