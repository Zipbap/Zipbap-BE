package zipbap.user.api.follow.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import zipbap.global.global.auth.resolver.UserInjection
import zipbap.user.api.follow.dto.FollowResponseDto
import zipbap.global.domain.user.User
import zipbap.app.global.ApiResponse

@RequestMapping("/api/follows")
@Tag(name = "Follow", description = "팔로우 API")
@Deprecated(message = "요구사항 변경시, Follow가 삭제 될 수 있습니다.")
interface FollowDocs {

    @Operation(
            summary = "유저 팔로우",
            description = """
followingId 유저를 팔로우 합니다. 

📌 파라미터 설명  
- **followingId (Long, path)**  
  - 팔로잉 할 유저 id (기본값 x)    

📌 호출 예시  
- `/api/followers/{followingId}`  
- `/api/followers/10`
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
                                    schema = Schema(implementation = FollowResponseDto.FollowCountDto::class)
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
    @PostMapping("/{followingId}")
    fun followUser(
            @UserInjection viewer: User,
            @PathVariable followingId: Long
    ): ApiResponse<FollowResponseDto.FollowCountDto>

    @Operation(
            summary = "유저 언팔로우(팔로우 취소)",
            description = """
followingId 유저를 언팔로우 합니다. 

📌 파라미터 설명  
- **unfollowingId (Long, path)**  
  - 언팔로잉 할 유저 id (기본값 x)    

📌 호출 예시  
- `/api/followers/{unfollowingId}`  
- `/api/followers/10`
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
                                    schema = Schema(implementation = FollowResponseDto.FollowCountDto::class)
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
    @DeleteMapping("/{unfollowingId}")
    fun unfollowUser(
            @UserInjection user: User,
            @PathVariable unfollowingId: Long
    ): ApiResponse<FollowResponseDto.FollowCountDto>

    @Operation(
            summary = "유저의 팔로워, 팔로잉 수 조회",
            description = """
userId 유저의 팔로워, 팔로잉 수, viewer의 팔로잉 여부를 조회합니다. 

📌 파라미터 설명  
- **userId (Long, path)**  
  - 팔로잉 할 유저 id (기본값 x)    

📌 호출 예시  
- `/api/followers/{userId}/count`  
- `/api/followers/5/count`
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
                                    schema = Schema(implementation = FollowResponseDto.FollowCountDto::class)
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
    @GetMapping("/{userId}/count")
    fun countFollow(
            @UserInjection user: User,
            @PathVariable userId: Long
    ): ApiResponse<FollowResponseDto.FollowCountDto>

    @Operation(
            summary = "유저 팔로잉 리스트 조회",
            description = """
userId의 팔로잉 목록을 조회합니다. 

📌 파라미터 설명  
- **userId (Long, path)**  
  - 팔로잉 리스트를 조회 할 유저 id (기본값 x)    

📌 호출 예시  
- `/api/followers/{userId}/following-list`  
- `/api/followers/10/following-list`
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
                                    array = ArraySchema(
                                            schema = Schema(
                                                    implementation = FollowResponseDto.FollowUserDto::class)
                                    )
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
    @GetMapping("/{userId}/following-list")
    fun getFollowingList(
            @UserInjection user: User,
            @PathVariable userId: Long,
            @RequestParam(required = false) condition: String?
    ): ApiResponse<List<FollowResponseDto.FollowUserDto>>

    @Operation(
            summary = "유저 팔로워 리스트 조회",
            description = """
userId의 팔로워 목록을 조회합니다. 

📌 파라미터 설명  
- **userId (Long, path)**  
  - 팔로워 리스트를 조회 할 유저 id (기본값 x)    

📌 호출 예시  
- `/api/followers/{userId}/follower-list`  
- `/api/followers/10/follower-list`
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
                                    array = ArraySchema(
                                            schema = Schema(
                                                    implementation = FollowResponseDto.FollowUserDto::class)
                                    )
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
    @GetMapping("/{userId}/follower-list")
    fun getFollowerList(
            @UserInjection user: User,
            @PathVariable userId: Long,
            @RequestParam(required = false) condition: String?):
            ApiResponse<List<FollowResponseDto.FollowUserDto>>
}