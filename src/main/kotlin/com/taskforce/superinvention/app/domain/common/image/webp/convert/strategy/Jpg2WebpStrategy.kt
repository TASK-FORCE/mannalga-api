package com.taskforce.superinvention.app.domain.common.image.webp.convert.strategy

import com.sksamuel.scrimage.ImmutableImage
import com.sksamuel.scrimage.webp.WebpWriter
import com.taskforce.superinvention.app.domain.common.image.ImageFormat
import com.taskforce.superinvention.app.domain.common.image.webp.convert.WebpCompressionParam
import com.taskforce.superinvention.common.util.kakao.KakaoOAuth
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@Service
class Jpg2WebpStrategy: WebpConvertStrategy {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(Jpg2WebpStrategy::class.java)
    }

    override fun identify(imageFormat: ImageFormat): Boolean {
        return imageFormat == ImageFormat.JPG ||
               imageFormat == ImageFormat.JPEG
    }

    override fun convert(fileName: String, file: File, param: WebpCompressionParam): File {

        return try {
            ImmutableImage.loader()
                .fromFile(file)
                .output(WebpWriter.DEFAULT, File("${fileName}.webp"))
        } catch (e: IOException) {
            LOG.error("JPG -> WebP간 변환 실패, 컨버팅을 취소합니다. {}", e)
            file
        }
    }
}
