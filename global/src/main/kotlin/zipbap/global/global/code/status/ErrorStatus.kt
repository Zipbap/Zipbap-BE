package zipbap.global.global.code.status

import org.springframework.http.HttpStatus
import zipbap.global.global.code.BaseErrorCode
import zipbap.global.global.code.ErrorReasonDto

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

    // 요리 종류 에러
    COOKING_TYPE_NOT_FOUND(HttpStatus.NOT_FOUND, "COOKING_TYPE404", "해당 요리 유형을 찾을 수 없습니다."),
    DUPLICATE_COOKING_TYPE(HttpStatus.CONFLICT, "COOKING_TYPE409", "이미 존재하는 요리 유형입니다."),

    // 요리 상황 에러
    SITUATION_NOT_FOUND(HttpStatus.NOT_FOUND, "SITUATION404", "해당 상황을 찾을 수 없습니다."),
    DUPLICATE_SITUATION(HttpStatus.CONFLICT, "SITUATION409", "이미 존재하는 상황입니다."),

    // 요리 주재료 에러
    MAIN_INGREDIENT_NOT_FOUND(HttpStatus.NOT_FOUND, "MAIN_INGREDIENT404", "해당 요리 주재료를 찾을 수 없습니다."),
    DUPLICATE_MAIN_INGREDIENT(HttpStatus.CONFLICT, "MAIN_INGREDIENT409", "이미 존재하는 요리 주재료입니다."),

    // 요리 방법
    METHOD_NOT_FOUND(HttpStatus.NOT_FOUND, "METHOD404", "해당 요리 방법을 찾을 수 없습니다."),
    DUPLICATE_METHOD(HttpStatus.CONFLICT, "METHOD409", "이미 존재하는 요리 방법입니다."),

    // 요리 인분(인원수)
    HEADCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "HEADCOUNT404", "해당 인분을 찾을 수 없습니다."),
    DUPLICATE_HEADCOUNT(HttpStatus.CONFLICT, "HEADCOUNT409", "이미 존재하는 인분입니다."),

    // 요리 시간
    COOKING_TIME_NOT_FOUND(HttpStatus.NOT_FOUND, "COOKING_TIME404", "해당 요리 시간을 찾을 수 없습니다."),
    DUPLICATE_COOKING_TIME(HttpStatus.CONFLICT, "COOKING_TIME409", "이미 존재하는 요리 시간입니다."),

    // 요리 난이도
    LEVEL_NOT_FOUND(HttpStatus.NOT_FOUND, "LEVEL404", "해당 난이도를 찾을 수 없습니다."),
    DUPLICATE_LEVEL(HttpStatus.CONFLICT, "LEVEL409", "이미 존재하는 난이도입니다."),

    // 내 카테고리 (사용자 전용 카테고리)
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "CATEGORY404", "해당 '내 카테고리'를 찾을 수 없습니다."),
    DUPLICATE_CATEGORY(HttpStatus.CONFLICT, "CATEGORY409", "이미 존재하는 '내 카테고리'입니다."),

    // 레시피
    RECIPE_NOT_FOUND(HttpStatus.NOT_FOUND, "RECIPE404", "해당 레시피를 찾을 수 없습니다."),
    RECIPE_FORBIDDEN(HttpStatus.FORBIDDEN, "RECIPE403", "해당 레시피에 대한 권한이 없습니다."),
    INVALID_RECIPE_STATUS(HttpStatus.BAD_REQUEST, "RECIPE400", "레시피 상태가 유효하지 않습니다."),
    RECIPE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "RECIPE400", "잘못된 레시피 요청입니다."),
    DUPLICATE_COOKING_ORDER_TURN(HttpStatus.BAD_REQUEST, "COOKING_ORDER400", "조리 순서(turn) 값이 중복되었습니다."),
    RECIPE_ALREADY_FINALIZED(HttpStatus.CONFLICT, "RECIPE409", "이미 최종 저장된 레시피입니다."),

    // 좋아요 관련 에러
    LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "LIKE404", "해당 좋아요를 찾을 수 없습니다."),
    ALREADY_LIKED_RECIPE(HttpStatus.CONFLICT, "LIKE409", "이미 좋아요를 누른 레시피입니다."),

    // 댓글 관련 에러
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMENT404", "해당 댓글을 찾을 수 없습니다."),
    COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "COMMENT403", "해당 댓글에 대한 권한이 없습니다."),

    // 북마크
    BOOKMARK_NOT_FOUND(HttpStatus.NOT_FOUND, "BOOKMARK404", "해당 북마크를 찾을 수 없습니다."),
    ALREADY_BOOKMARK_RECIPE(HttpStatus.CONFLICT, "BOOKMARK409", "이미 북마크한 레시피입니다."),

    // 팔로우
    ALREADY_FOLLOW_EXIST(HttpStatus.CONFLICT, "FOLLOW409", "이미 존재하는 팔로잉 관계입니다."),
    FOLLOW_NOT_FOUND(HttpStatus.NOT_FOUND, "FOLLOW404", "해당 팔로잉 관계를 찾을 수 없습니다."),

    // Auth
    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, "AUTHENTICATION401", "인증에 실패했습니다."),
    OAUTH2_LOGIN_FAIL(HttpStatus.UNAUTHORIZED, "LOGIN401", "OAuth2 Login 과정에서 에러가 발생했습니다."),
    INVALID_REGISTRATION(HttpStatus.BAD_REQUEST, "REGISTRATION400", "잘못된 REGISTRATION입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER404", "해당 유저를 찾을 수 없습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "TOKEN401", "잘못되거나 만료된 토큰입니다.");

    override val reason: ErrorReasonDto
        get() = ErrorReasonDto(httpStatus, false, code, message)

    override val reasonHttpStatus: ErrorReasonDto
        get() = ErrorReasonDto(httpStatus, false, code, message)
}
