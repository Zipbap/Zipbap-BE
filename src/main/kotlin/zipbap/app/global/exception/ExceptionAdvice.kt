package zipbap.app.global.exception


import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import zipbap.app.global.ApiResponse
import zipbap.app.global.code.ErrorReasonDto
import zipbap.app.global.code.status.ErrorStatus
import java.util.*
import java.util.function.Consumer

@RestControllerAdvice
class ExceptionAdvice : ResponseEntityExceptionHandler() {

    /**
     * @Valid 어노테이션을 통한 검증 실패시 작동하는 메소드입니다.
     * ConstraintViolationException은 @RequestParam, @PathVariable을 사용한
     * 파라미터에서 검증 실패시 발생합니다.
     */
    @ExceptionHandler
    fun validation(e: ConstraintViolationException, request: WebRequest): ResponseEntity<Any>? {
        val errorMessage: String = e.constraintViolations.stream()
                .map { constraintViolation -> constraintViolation.message }
                .findFirst()
                .orElseThrow { RuntimeException("ConstraintViolationException 추출 도중 에러 발생") }

        return handleExceptionInternalConstraint(e, ErrorStatus.valueOf(errorMessage), HttpHeaders.EMPTY, request)
    }


    /**
     * @Valid 어노테이션을 통한 검증 실패시 작동하는 메소드입니다.
     * MethodArgumentNotValidException는 @RequestBody를 사용한 파라미터에서 검증 실패시 발생합니다.
     */
    protected override fun handleMethodArgumentNotValid(
            ex: MethodArgumentNotValidException, headers: HttpHeaders, status: HttpStatusCode, request: WebRequest): ResponseEntity<Any>? {
        val errors: MutableMap<String, String> = LinkedHashMap()

        ex.bindingResult.fieldErrors
                .forEach(Consumer<FieldError> { fieldError: FieldError ->
                    val fieldName: String = fieldError.field
                    val errorMessage = Optional.ofNullable<String>(fieldError.defaultMessage).orElse("")
                    errors.merge(fieldName, errorMessage) { existingErrorMessage: String, newErrorMessage: String -> "$existingErrorMessage, $newErrorMessage" }
                })

        return handleExceptionInternalArgs(ex, HttpHeaders.EMPTY, ErrorStatus.BAD_REQUEST, request, errors)
    }


    /**
     * 일반적으로 Java에서 발생하는 에러 발생시 작동하는 메소드입니다.
     * ex) IllegalArgumentException
     */
    @ExceptionHandler
    fun exception(e: Exception, request: WebRequest): ResponseEntity<Any>? {
        e.printStackTrace()

        return handleExceptionInternalFalse(e, ErrorStatus.INTERNAL_SERVER_ERROR, HttpHeaders.EMPTY, ErrorStatus.INTERNAL_SERVER_ERROR.httpStatus, request, e.message)
    }

    /**
     * GeneralException이 발생하면 작동하는 메소드입니다.
     * GeneralException은 특정 자원을 요청했는데 없을 경우 주로 발생시킵니다.
     * ex) userRepository.findById(id).orElseThrow(::GeneralException)
     * 주로 개발자가 미리 정의한 ErrorStatus를 담아서 발생합니다.
     */
    @ExceptionHandler(value = [GeneralException::class])
    fun onThrowException(generalException: GeneralException, request: HttpServletRequest): ResponseEntity<Any>? {
        val errorReasonHttpStatus: ErrorReasonDto = generalException.errorReasonHttpStatus
        return handleExceptionInternal(generalException, errorReasonHttpStatus, HttpHeaders.EMPTY, request)
    }

    private fun handleExceptionInternal(e: Exception, reason: ErrorReasonDto,
                                        headers: HttpHeaders?, request: HttpServletRequest): ResponseEntity<Any>? {
        val body: ApiResponse<Any> = ApiResponse.onFailure(reason.code, reason.message, null)

        val webRequest: WebRequest = ServletWebRequest(request)
        return super.handleExceptionInternal(
                e,
                body,
                headers ?: HttpHeaders.EMPTY,
                reason.httpStatus,
                webRequest
        )
    }

    private fun handleExceptionInternalFalse(e: Exception, errorCommonStatus: ErrorStatus,
                                             headers: HttpHeaders, status: HttpStatus, request: WebRequest, errorPoint: String?): ResponseEntity<Any>? {
        val body: ApiResponse<Any> = ApiResponse.onFailure(errorCommonStatus.code, errorCommonStatus.message, errorPoint)
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                status,
                request
        )
    }

    private fun handleExceptionInternalArgs(e: Exception, headers: HttpHeaders, errorCommonStatus: ErrorStatus,
                                            request: WebRequest, errorArgs: Map<String, String>): ResponseEntity<Any>? {
        val body: ApiResponse<Any> = ApiResponse.onFailure(errorCommonStatus.code, errorCommonStatus.message, errorArgs)
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                errorCommonStatus.httpStatus,
                request
        )
    }

    private fun handleExceptionInternalConstraint(e: Exception, errorCommonStatus: ErrorStatus,
                                                  headers: HttpHeaders, request: WebRequest): ResponseEntity<Any>? {
        val body: ApiResponse<Any> = ApiResponse.onFailure(errorCommonStatus.code, errorCommonStatus.message, null)
        return super.handleExceptionInternal(
                e,
                body,
                headers,
                errorCommonStatus.httpStatus,
                request
        )
    }
}
