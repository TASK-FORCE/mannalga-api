package com.taskforce.superinvention.common.util.aws.s3

data class S3Path (
    var absolutePath: String = "",
    var filePath    : String = "",
    var fileName    : String = ""
) {
    fun folderPath(): String {
        return filePath.replace("/${fileName}", "")
    }
}

fun S3Path.isValidPath(): Boolean {
    return this.absolutePath.isNotBlank() &&
           this.filePath.isNotBlank()     &&
           this.fileName.isNotBlank()
}
