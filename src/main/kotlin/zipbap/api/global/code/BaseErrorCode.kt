package zipbap.api.global.code

interface BaseErrorCode {
    val reason: ErrorReasonDto
    val reasonHttpStatus: ErrorReasonDto
}
