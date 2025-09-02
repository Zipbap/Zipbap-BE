package zipbap.api.global.exception

import zipbap.api.global.code.BaseErrorCode
import zipbap.api.global.code.ErrorReasonDto

class GeneralException(
        val code: BaseErrorCode
) : RuntimeException() {

    val errorReason: ErrorReasonDto
        get() = this.code.reason

    val errorReasonHttpStatus: ErrorReasonDto
        get() = this.code.reasonHttpStatus
}
