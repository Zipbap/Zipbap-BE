package zipbap.app.global.code.status

import org.springframework.http.HttpStatus
import zipbap.app.global.code.BaseErrorCode
import zipbap.app.global.code.ErrorReasonDto

enum class ErrorStatus(
        val httpStatus: HttpStatus,
        val code: String,
        val message: String
) : BaseErrorCode {
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT404", "결제 수단을 찾을 수 없습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다.");

    override val reason: ErrorReasonDto
        get() = ErrorReasonDto(httpStatus, false, code, message)

    override val reasonHttpStatus: ErrorReasonDto
        get() = ErrorReasonDto(httpStatus, false, code, message)
}
