package zipbap.app.global.code

import org.springframework.http.HttpStatus

data class ReasonDto(
     val httpStatus: HttpStatus? = null,
     val isSuccess: Boolean = false,
     val code: String? = null,
     val message: String? = null
) {
}
