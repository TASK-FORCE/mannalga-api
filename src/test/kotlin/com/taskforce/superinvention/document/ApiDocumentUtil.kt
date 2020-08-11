package com.taskforce.superinvention.document

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors.*

object ApiDocumentUtil {

    fun getDocumentRequest() : OperationRequestPreprocessor {
        return preprocessRequest(
            modifyUris()
                    .scheme("http")
                    .host("ec2-52-78-18-217.ap-northeast-2.compute.amazonaws.com")
                    .port(8080),
                prettyPrint()
        )
    }

    fun getDocumentResponse(): OperationResponsePreprocessor {
        return preprocessResponse(prettyPrint())
    }
}