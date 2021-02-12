package com.taskforce.superinvention.app.domain.common.image.webp.convert.strategy

import com.taskforce.superinvention.app.domain.common.image.ImageFormat
import com.taskforce.superinvention.app.domain.common.image.webp.convert.WebpCompressionParam
import java.io.File

interface WebpConvertStrategy {
    fun identify(imageFormat: ImageFormat): Boolean
    fun convert(fileName: String, file: File, param: WebpCompressionParam): File
}