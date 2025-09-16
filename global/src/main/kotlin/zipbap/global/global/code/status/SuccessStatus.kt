package zipbap.global.global.code.status

import org.springframework.http.HttpStatus
import zipbap.global.global.code.BaseCode
import zipbap.global.global.code.ReasonDto

enum class SuccessStatus(
        val httpStatus: HttpStatus,
        val code: String,
        val message: String

) : BaseCode {

    _OK(HttpStatus.OK, "COMMON100", "성공입니다.");


    override val reasonDto: ReasonDto
        get() = ReasonDto(httpStatus, true, code, message)

    override val reasonHttpStatus: ReasonDto
        get() = ReasonDto(httpStatus, true, code, message)
}
