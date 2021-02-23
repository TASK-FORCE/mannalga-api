package com.taskforce.superinvention.app.domain.common.image.resize.strategy

import com.taskforce.superinvention.app.domain.common.image.ImageFormat
import com.taskforce.superinvention.app.web.dto.common.image.ResizeDto
import java.io.File

interface ImageResizeStrategy {
    fun identify(imageFormat: ImageFormat): Boolean
    fun resize(file: File, resize: ResizeDto): File
}