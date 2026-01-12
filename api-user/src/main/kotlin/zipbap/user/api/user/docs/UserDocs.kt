package zipbap.user.api.user.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import zipbap.global.global.auth.resolver.UserInjection
import zipbap.user.api.user.dto.UserRequestDto
import zipbap.user.api.user.dto.UserResponseDto
import zipbap.global.domain.user.User
import zipbap.app.global.ApiResponse

@Tag(name = "User", description = "User API")
@RequestMapping("/api/users")
interface UserDocs {


    @Operation(
            summary = "유저 프로필 조회(프로필 편집 진입)",
            description = """
유저의 프로필을 조회합니다. 

📌 파라미터 설명  
- 파라미터 없음

📌 호출 예시  
- `/api/users/profile  
"""
    )
    @ApiResponses(
            value = [
                io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "조회 성공",
                        content = [
                            Content(
                                    mediaType = "application/json",
                                    schema = Schema(implementation = UserResponseDto.UserProfileDto::class)
                            )
                        ]
                ),
                io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "401",
                        description = "로그인 필요 (북마크 진행시 필요)",
                        content = [Content(schema = Schema(implementation = ApiResponse::class))]
                )
            ]
    )
    @GetMapping("/profile")
    fun getProfile(
            @UserInjection userId: Long
    ): ApiResponse<UserResponseDto.UserProfileDto>

    @Operation(
            summary = "유저 프로필 수정",
            description = """
유저의 프로필을 수정합니다. 

📌 파라미터 설명  
- `nickname`: 유저의 닉네임입니다. blank 및 null 불가
- `isPrivate`: 유저의 계정 공개여부입니다. true -> 계정 비공개, false -> 계정 공개
- `profileImage`: 유저의 프로필 이미지입니다. required X
- `statusMessage`: 유저의 상태메시지 입니다. required X

📌 호출 예시  
- `/api/users/profile  
"""
    )
    @ApiResponses(
            value = [
                io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "조회 성공",
                        content = [
                            Content(
                                    mediaType = "application/json",
                                    schema = Schema(implementation = UserResponseDto.UserProfileDto::class)
                            )
                        ]
                ),
                io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "401",
                        description = "로그인 필요 (북마크 진행시 필요)",
                        content = [Content(schema = Schema(implementation = ApiResponse::class))]
                )
            ]
    )
    @PutMapping("/profile")
    fun updateProfile(
            @UserInjection userId: Long,
            @RequestBody dto: UserRequestDto.UserUpdateDto
    ): ApiResponse<UserResponseDto.UserProfileDto>




    @Operation(
        summary = "회원 탈퇴",
        description = """
유저 본인의 계정을 삭제합니다.

📌 주의사항  
- 유저와 관련된 모든 데이터는 즉시 삭제됩니다.
"""
    )
    @ApiResponses(
        value = [
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "200",
                description = "탈퇴 완료",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = ApiResponse::class)
                    )
                ]
            ),
            io.swagger.v3.oas.annotations.responses.ApiResponse(
                responseCode = "401",
                description = "로그인 필요",
                content = [Content(schema = Schema(implementation = ApiResponse::class))]
            )
        ]
    )
    @DeleteMapping
    fun deleteUser(
        @UserInjection userId: Long
    ): ApiResponse<String>


}