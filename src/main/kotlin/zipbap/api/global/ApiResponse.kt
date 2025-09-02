package zipbap.api.global

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import zipbap.api.global.code.BaseCode
import zipbap.api.global.code.status.SuccessStatus

@JsonPropertyOrder("isSuccess", "code", "message", "result")
class ApiResponse<T>(
    val isSuccess: Boolean = false,
    val code: String? = null,
    val message: String? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL) // Null이 아닌 경우에만 포함됩니다.
    val result: T? = null
) {


    companion object {
        fun <T> onSuccess(result: T): ApiResponse<T> {
            return ApiResponse<T>(true, SuccessStatus._OK.code, SuccessStatus._OK.message, result)
        }

        fun <T> of(code: BaseCode, result: T): ApiResponse<T> {
            return ApiResponse(true, code.reasonHttpStatus.code, code.reasonHttpStatus.message, result)
        }

        fun <T> onFailure(code: String?, message: String?, data: T?): ApiResponse<T> {
            return ApiResponse(false, code, message, data)
        }
    }
}
