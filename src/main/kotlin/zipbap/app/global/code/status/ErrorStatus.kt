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
    FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    // 요리상황 종류
    COOKING_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "COOKING_TYPE404", "해당 요리 유형을 찾을 수 없습니다."),
    DUPLICATE_COOKING_TYPE(HttpStatus.CONFLICT, "COOKING_TYPE409", "이미 존재하는 요리 유형입니다."),

    // 요리상황 에러
    SITUATION_NOT_FOUND(HttpStatus.NOT_FOUND, "SITUATION404", "해당 상황을 찾을 수 없습니다."),
    DUPLICATE_SITUATION(HttpStatus.CONFLICT, "SITUATION409", "이미 존재하는 상황입니다.");

    override val reason: ErrorReasonDto
        get() = ErrorReasonDto(httpStatus, false, code, message)

    override val reasonHttpStatus: ErrorReasonDto
        get() = ErrorReasonDto(httpStatus, false, code, message)
}
