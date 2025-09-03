package zipbap.app.global.exception

import zipbap.app.global.code.BaseErrorCode
import zipbap.app.global.code.ErrorReasonDto

class GeneralException(
        val code: BaseErrorCode
) : RuntimeException() {

    val errorReason: ErrorReasonDto
        get() = this.code.reason

    val errorReasonHttpStatus: ErrorReasonDto
        get() = this.code.reasonHttpStatus
}
