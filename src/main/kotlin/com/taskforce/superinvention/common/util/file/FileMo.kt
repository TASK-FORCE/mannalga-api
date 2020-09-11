package com.taskforce.superinvention.common.util.file

import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class FileMo {
    companion object {

        fun convertMultiPartToFile(file: MultipartFile): File {
            val convertedFile = File(file.originalFilename!!)
            val fos = FileOutputStream(convertedFile)
            fos.write(file.bytes)
            fos.close()
            return convertedFile
        }

        fun generateUUID(multiPart: MultipartFile): String {
            val datetimeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_hh_mm_ss"))

            val uuid = "${datetimeStr}_${UUID.randomUUID()}"
            val fileName = multiPart.originalFilename!!.replace(" ".toRegex(), "_")

            return "${uuid}_${fileName}"
        }

    }
}