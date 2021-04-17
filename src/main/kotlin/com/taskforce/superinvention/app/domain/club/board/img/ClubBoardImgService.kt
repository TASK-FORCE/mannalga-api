package com.taskforce.superinvention.app.domain.club.board.img

import com.taskforce.superinvention.app.domain.club.board.ClubBoard
import com.taskforce.superinvention.app.domain.common.image.webp.convert.WebpConvertService
import com.taskforce.superinvention.app.web.dto.club.board.ClubBoardImgDto
import com.taskforce.superinvention.app.web.dto.club.board.img.ClubBoardImgEditS3Path
import com.taskforce.superinvention.common.util.aws.s3.AwsS3Mo
import com.taskforce.superinvention.common.util.aws.s3.S3Path
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ClubBoardImgService(
    private val awsS3Mo: AwsS3Mo,
    private val clubBoardImgRepository: ClubBoardImgRepository,
    private val webpConvertService: WebpConvertService,

    @Value("\${host.static.path}")
    private var imgHost: String,
) {

    @Transactional
    fun registerImg(clubBoard: ClubBoard, imgList: List<S3Path>): List<ClubBoardImg> {
        val imgFolder = "clubBoardImg/${clubBoard.seq}"

        // [1] 기존 임시 저장된 파일들을 해당 폴더로 이동
        val clubBoardImgList = imgList.mapIndexed{ idx, s3Path ->
            val movedFile: S3Path = awsS3Mo.moveFile(s3Path, "$imgFolder/${s3Path.fileName}")
            webpConvertService.convertToWebP(movedFile)

            ClubBoardImg(
                imgName      = movedFile.fileName,
                imgUrl       = movedFile.filePath,
                clubBoard    = clubBoard,
                displayOrder = idx + 1L,
                deleteFlag   = false
            )
        }

        clubBoardImgRepository.saveAll(clubBoardImgList)
        return clubBoardImgList
    }

    @Transactional
    fun registerImg(clubBoard: ClubBoard, s3Path: S3Path, order: Long): ClubBoardImg {
        val imgFolder = "clubBoardImg/${clubBoard.seq}"

        // [1] 기존 임시 저장된 파일들을 해당 폴더로 이동
        val movedFile: S3Path = awsS3Mo.moveFile(s3Path, "$imgFolder/${s3Path.fileName}")
        webpConvertService.convertToWebP(movedFile)

        val clubBoardImg = ClubBoardImg(
            imgName = movedFile.fileName,
            imgUrl = movedFile.filePath,
            clubBoard = clubBoard,
            displayOrder = order,
            deleteFlag = false
        )

        return clubBoardImgRepository.save(clubBoardImg)
    }

    @Transactional
    fun getImageList(clubBoard: ClubBoard): List<ClubBoardImgDto> {
       return clubBoardImgRepository.findByClubBoardOrderByOrderAsc(clubBoard)
           .map { clubBoardImg -> ClubBoardImgDto(
               imgHost      = imgHost,
               clubBoardImg = clubBoardImg
           )}
    }

    @Transactional
    fun softDeleteImageBySeqIn(clubBoardImgSeqList: List<Long>) {
        val imgList =  clubBoardImgRepository.findBySeqIn(clubBoardImgSeqList)

        imgList.forEach{ clubBoardImg -> clubBoardImg.deleteFlag = true}
    }

    @Transactional
    fun softDeleteImageAllInClubBoard(clubBoard: ClubBoard) {
        val imgList =  clubBoardImgRepository.findByClubBoard(clubBoard)

        imgList.forEach{ clubBoardImg ->
            clubBoardImg.deleteFlag = true
            clubBoardImg.displayOrder      = null
        }

        // [1] 해당 이미지 DB에서 제거 / flag 처리
        clubBoardImgRepository.saveAll(imgList)
    }

    @Transactional
    fun editClubBoardImages(clubBoard: ClubBoard, editImageList: List<ClubBoardImgEditS3Path>) {
        val clubBoardImgs = clubBoardImgRepository.findByClubBoardOrderByOrderAsc(clubBoard)
        val overrideImgs  = editImageList.filter { img -> img.imgSeq != null }
                                         .map{ editS3Path-> editS3Path.imgSeq!!}
                                         .toList()

        // editImageList에 없는 이미지는 삭제처리
        for (clubBoardImg in clubBoardImgs) {
            if(!overrideImgs.contains(clubBoardImg.seq)) {
                clubBoardImg.deleteFlag = true
                clubBoardImg.displayOrder      = null
            }
        }

        // 이미지 순차 저장 처리
        var order = 1L
        for(editS3Path in editImageList) {

            if(editS3Path.imgSeq != null) {
                val clubBoardImg = clubBoardImgs.firstOrNull { clubBoardImg -> clubBoardImg.seq == editS3Path.imgSeq }
                if(clubBoardImg != null) {
                    clubBoardImg.displayOrder = order
                    order++
                }
            } else {
                registerImg(
                    clubBoard = clubBoard,
                    s3Path    = editS3Path.image,
                    order     = order
                )
            }
        }
    }

}
