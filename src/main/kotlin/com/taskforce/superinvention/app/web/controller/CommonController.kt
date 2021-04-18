package com.taskforce.superinvention.app.web.controller

import com.taskforce.superinvention.app.domain.common.FileService
import com.taskforce.superinvention.app.domain.common.image.ImageService
import com.taskforce.superinvention.app.web.common.response.ResponseDto
import com.taskforce.superinvention.app.web.dto.common.image.ResizeDto
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RequestMapping("/common")
@RestController
class CommonController(
    private val fileService : FileService,
    private val imageService: ImageService
) {
    @PostMapping("/temp/image")
    fun imageTempSave(file: MultipartFile, resize: ResizeDto): ResponseDto<S3Path> {
        return ResponseDto(data = imageService.fileImageSave(file, resize))
    }

    @PostMapping("/temp/file")
    fun fileTempSave(file: MultipartFile): ResponseDto<S3Path> {
        return ResponseDto(data = fileService.fileTempSave(file))
    }
}