package com.taskforce.superinvention.common.util.file.image.gif

import com.drew.imaging.ImageMetadataReader
import com.drew.metadata.Metadata
import com.drew.metadata.gif.GifControlDirectory
import java.io.ByteArrayInputStream

class GifMo {
    companion object {
        fun isAnimated(metadata: Metadata): Boolean {
            val controlDirectories = metadata.getDirectoriesOfType(GifControlDirectory::class.java) as List<*>

            return controlDirectories.size > 1
        }
    }
}