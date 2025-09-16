package zipbap.global.global.exception

import zipbap.global.global.code.BaseErrorCode
import zipbap.global.global.code.ErrorReasonDto

class GeneralException(
        val code: BaseErrorCode
) : RuntimeException() {

    val errorReason: ErrorReasonDto
        get() = this.code.reason

    val errorReasonHttpStatus: ErrorReasonDto
        get() = this.code.reasonHttpStatus
}
