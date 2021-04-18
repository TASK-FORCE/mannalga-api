package com.taskforce.superinvention.app.domain.common.image.resize.strategy

import com.taskforce.superinvention.app.domain.common.image.ImageFormat
import com.taskforce.superinvention.app.web.dto.common.image.ResizeDto
import org.springframework.stereotype.Service
import java.io.File

@Service
class WebpResizeStrategy: ImageResizeStrategy {

    override fun identify(imageFormat: ImageFormat): Boolean {
        return imageFormat == ImageFormat.WEBP
    }

    override fun resize(file: File, resize: ResizeDto): File {
        // @Todo webp는 resizing 하지 않음
        return file
    }
}