package zipbap.global.global.code

import org.springframework.http.HttpStatus

data class ErrorReasonDto(
    val httpStatus: HttpStatus,
    val isSuccess: Boolean,
    val code: String,
    val message: String
) {
}
