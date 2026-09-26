package zipbap.user.api.boomark.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*
import zipbap.global.global.auth.resolver.UserInjection
import zipbap.user.api.boomark.dto.BookmarkResponseDto
import zipbap.global.domain.user.User
import zipbap.app.global.ApiResponse

@RequestMapping("/api/bookmarks")
@Tag(name = "Bookmark", description = "북마크 API (레시피 게시물 조회)")
interface BookmarkDocs {

    @Operation(
            summary = "레시피에 북마크 등록",
            description = """
해당 recipeId를 가진 레시피를 북마크에 등록합니다. 

📌 파라미터 설명  
- **recipeId (Long, path)**  
  - 북마크 등록 대상 (기본값 x)    

📌 호출 예시  
- `/api/bookmarks/{recipeId}`  
- `/api/bookmarks/5`
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
                                    schema = Schema(implementation = BookmarkResponseDto.BookmarkSimpleResponseDto::class)
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
    @PostMapping("/{recipeId}")
    fun markRecipe(@UserInjection userId: Long,
                   @PathVariable recipeId: String
    ): ApiResponse<BookmarkResponseDto.BookmarkSimpleResponseDto>


    @Operation(
            summary = "레시피에 등록된 북마크 삭제",
            description = """
해당 recipeId를 가진 레시피에 등록된 유저의 북마크를 제거합니다. 

📌 파라미터 설명  
- **recipeId (Long, path)**  
  - 북마크 삭제 대상 (기본값 x)    

📌 호출 예시  
- `/api/bookmarks/{recipeId}`  
- `/api/bookmarks/5`
"""
    )
    @ApiResponses(
            value = [
                io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "삭제 성공",
                        content = [
                            Content(
                                    mediaType = "application/json",
                                    schema = Schema(implementation = BookmarkResponseDto.BookmarkSimpleResponseDto::class)
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
    @DeleteMapping("/{recipeId}")
    fun unmarkRecipe(@UserInjection userId: Long,
                     @PathVariable recipeId: String
    ): ApiResponse<BookmarkResponseDto.BookmarkSimpleResponseDto>

    @Operation(
            summary = "레시피의 북마크 수를 반환",
            description = """
해당 recipeId를 가진 레시피의 등록된 북마크 수를 반환합니다. 

📌 파라미터 설명  
- **recipeId (Long, path)**  
  - 북마크 count 조회 대상 (기본값 x)    

📌 호출 예시  
- `/api/bookmarks/{recipeId}/count`  
- `/api/bookmarks/5/count`
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
                                    schema = Schema(implementation = BookmarkResponseDto.BookmarkSimpleResponseDto::class)
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
    @GetMapping("/{recipeId}/count")
    fun countBookmarks(@UserInjection userId: Long, @PathVariable recipeId: String
    ): ApiResponse<BookmarkResponseDto.BookmarkSimpleResponseDto>

    @Operation(
            summary = "유저의 북마크된 레시피 조회",
            description = """
유저가 북마크로 등록한 레시피들을 조회합니다. 

📌 파라미터 설명  
    - 파라미터 없음
    - 현재 로그인된 유저의 시점에서 실행
    
📌 호출 예시  
- `/api/bookmarks/users`  
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
                                    schema = Schema(implementation = BookmarkResponseDto.BookmarkRecipeResponseDto::class)
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
    @GetMapping("/users")
    fun userRecipes(@UserInjection userId: Long
    ): ApiResponse<List<BookmarkResponseDto.BookmarkRecipeResponseDto>>
}
