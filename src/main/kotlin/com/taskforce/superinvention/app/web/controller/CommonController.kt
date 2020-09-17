package com.taskforce.superinvention.app.web.controller

import com.taskforce.superinvention.app.domain.common.FileService
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MultipartFile

@RequestMapping("/common")
@RestControllerAdvice
class CommonController(
        val fileService: FileService
) {
    @PostMapping("/temp/file")
    fun fileTempSave(file: MultipartFile): S3Path {
        return fileService.fileTempSave(file)
    }
}