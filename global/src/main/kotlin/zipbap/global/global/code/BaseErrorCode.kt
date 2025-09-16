package zipbap.global.global.code

interface BaseErrorCode {
    val reason: ErrorReasonDto
    val reasonHttpStatus: ErrorReasonDto
}
