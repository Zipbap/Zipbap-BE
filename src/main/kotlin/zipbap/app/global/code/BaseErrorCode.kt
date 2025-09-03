package zipbap.app.global.code

interface BaseErrorCode {
    val reason: ErrorReasonDto
    val reasonHttpStatus: ErrorReasonDto
}
