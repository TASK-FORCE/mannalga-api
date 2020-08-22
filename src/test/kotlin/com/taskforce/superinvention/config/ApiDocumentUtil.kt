package com.taskforce.superinvention.config

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors.*

object ApiDocumentUtil {

    fun getDocumentRequest() : OperationRequestPreprocessor {
        return preprocessRequest(
            modifyUris()
                    .scheme("http")
                    .host("52.78.18.217")
                    .removePort(),
                prettyPrint()
        )
    }

    fun getDocumentResponse(): OperationResponsePreprocessor {
        return preprocessResponse(prettyPrint())
    }
}