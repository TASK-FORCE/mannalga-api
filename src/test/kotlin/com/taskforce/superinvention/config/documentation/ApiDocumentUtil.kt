package com.taskforce.superinvention.config.documentation

import org.springframework.data.domain.Pageable
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.request.RequestDocumentation.*

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

    /**
     * 공통 페이지 모듈 리스폰스 포맷
     */
    fun pageFieldDescriptor(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("data.pageable").type(JsonFieldType.OBJECT).description("페이징 정보"),

            fieldWithPath("data.pageable.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
            fieldWithPath("data.pageable.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
            fieldWithPath("data.pageable.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 안했는지 여부"),
            fieldWithPath("data.pageable.sort.empty").type(JsonFieldType.BOOLEAN).description("빈 데이터인지 여부"),

            fieldWithPath("data.pageable.offset").type(JsonFieldType.NUMBER).description("요청한 오프셋(몇 페이지인지, 0부터 시작)"),
            fieldWithPath("data.pageable.pageNumber").type(JsonFieldType.NUMBER).description("몇 번째 페이지인지"),
            fieldWithPath("data.pageable.pageSize").type(JsonFieldType.NUMBER).description("요청한 페이지의 사이즈"),
            fieldWithPath("data.pageable.unpaged").type(JsonFieldType.BOOLEAN).description("페이징을 하지 않았는지 여부"),
            fieldWithPath("data.pageable.paged").type(JsonFieldType.BOOLEAN).description("페이징 여부"),

            fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("총 데이터 개수"),
            fieldWithPath("data.last").type(JsonFieldType.BOOLEAN).description("마지막 페이지인지 여부"),
            fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("요청한 오프셋으로 페이징할 때 총 페이지 개수"),
            fieldWithPath("data.number").type(JsonFieldType.NUMBER).description("현재 페이지 넘버"),
            fieldWithPath("data.size").type(JsonFieldType.NUMBER).description("페이징된 데이터 개수"),
            fieldWithPath("data.sort").type(JsonFieldType.OBJECT).description("정렬 정보"),
            fieldWithPath("data.sort.sorted").type(JsonFieldType.BOOLEAN).description("정렬 여부"),
            fieldWithPath("data.sort.unsorted").type(JsonFieldType.BOOLEAN).description("정렬 안했는지 여부"),
            fieldWithPath("data.sort.empty").type(JsonFieldType.BOOLEAN).description("빈 데이터인지 여부"),

            fieldWithPath("data.numberOfElements").type(JsonFieldType.NUMBER).description("응답 데이터 개수"),
            fieldWithPath("data.first").type(JsonFieldType.BOOLEAN).description("첫번째 페이지인지 여부"),
            fieldWithPath("data.empty").type(JsonFieldType.BOOLEAN).description("페이지가 비어있는지 여부")
        )
    }

    /**
     * 공통 응답 리스폰스 포맷
     * data 하위 엘리먼트는 각 테트별로 별도 정의
     */
    fun commonResponseField(): Array<FieldDescriptor> {
        return arrayOf(
            fieldWithPath("data").type(JsonFieldType.VARIES).description("데이터 본문"),
            fieldWithPath("message").type(JsonFieldType.STRING).description("결과 메세지")
        )
    }

    /**
     * 공통 페이징 쿼리파라미터
     */
    fun commonPageQueryParam(): Array<ParameterDescriptor> {
        return arrayOf(
                parameterWithName("page").description("페이지"),
                parameterWithName("size").description("조회 개수")
        )
    }

    fun eqPage(paramPageable: Pageable?, pageable: Pageable): Boolean {
        if(paramPageable == null) return false

        return when {
            paramPageable.pageSize == pageable.pageSize
                    && paramPageable.pageNumber == pageable.pageNumber -> true
            else -> false
        }
    }
}