package com.taskforce.superinvention.app.domain.common.image.webp.convert.strategy

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.webp.WebpWriter
import com.taskforce.superinvention.app.domain.common.image.ImageFormat
import com.taskforce.superinvention.app.domain.common.image.webp.convert.WebpCompressionParam
import org.apache.commons.io.FileUtils
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream

@Service
class Png2WebpStrategy: WebpConvertStrategy {

    override fun identify(imageFormat: ImageFormat): Boolean {
        return imageFormat == ImageFormat.PNG
    }

    override fun convert(fileName: String, file: File, param: WebpCompressionParam): File {
        return ImmutableImage.loader()
            .fromFile(file)
            .output(WebpWriter.DEFAULT, File("${fileName}.webp"))
    }
}