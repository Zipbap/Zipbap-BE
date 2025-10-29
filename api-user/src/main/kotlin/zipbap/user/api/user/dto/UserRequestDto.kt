package zipbap.user.api.user.dto

import io.swagger.v3.oas.annotations.media.Schema

class UserRequestDto {

    data class UserUpdateDto(
            @Schema(description = "변경할 유저의 닉네임", example = "김선우")
            val nickname: String,
            @Schema(description = "비공개 계정 여부", example = "false")
            val isPrivate: Boolean,
            @Schema(description = "유저 프로필 사진", example = "https://naver.com/image")
            val profileImage: String?,
            @Schema(description = "유저의 상태 메시지", example = "집 가고 싶다...")
            val statusMessage: String?
    )
}