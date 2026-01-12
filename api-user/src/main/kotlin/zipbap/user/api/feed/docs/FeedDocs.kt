package zipbap.user.api.feed.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.*
import zipbap.global.global.auth.resolver.UserInjection
import zipbap.user.api.feed.dto.FeedResponseDto
import zipbap.global.domain.user.User
import zipbap.app.global.ApiResponse
import zipbap.global.domain.feed.FeedFilterType
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

@RequestMapping("/api/feed")
@Tag(name = "Feed", description = "피드 API (레시피 게시물 조회)")
interface FeedDocs {

    @Operation(
        summary = "피드 목록 조회",
        description = """
조건에 따라 피드 목록을 페이징해서 가져옵니다.  
무한 스크롤 시 `page` 값만 증가시켜 호출하면 됩니다.  

📌 파라미터 설명  
- **filter (string, query)**  
  - 피드 조회 조건 (기본값: `ALL`)  
  - `ALL`: 전체  
  - `TODAY`: 오늘 올린 글만  
  - `HOT`: 좋아요 많은 순  
  - `RECOMMEND`: 북마크 많은 순  
  - `FOLLOWING`: 내가 팔로잉한 사람 글만 (로그인 필요)  

- **page (int, query)**  
  - 페이지 번호 (0부터 시작)  
  - 예: `page=0` → 첫 페이지, `page=1` → 두 번째 페이지  

- **size (int, query)**  
  - 한 페이지에 가져올 데이터 개수 (기본값: 20)  
  - 예: `size=10` → 10개씩 불러오기  

- **sort (array, query)**  
  - 정렬 조건 (기본값: `createdAt,desc`)  
  - 형식: `property,(asc|desc)`  
  - 예: `sort=createdAt,desc` → 최신순  
  - 예: `sort=likeCount,desc` → 좋아요 많은 순  
  - 여러 조건 가능 → `sort=likeCount,desc&sort=createdAt,desc` 
- **condition (string, query)**
    - 검색 조건
    - 제목, 소제목, 재료에서 해당 condition이 들어간 대상 검색
    - 제목, 소제목, 재료 순으로 정렬 순위를 가짐
    - null 일 경우 조건없이 검색
    - 예: `condition=소고기` -> 1. 제목에서 소고기가 들어간 피드 2. 소제목에서 소고기가 들어간 피드 3. 재료에서 소고기가 들어간 피드

📌 호출 예시  
- `/api/feed?filter=ALL&page=0&size=10&sort=createdAt,desc`  
- `/api/feed?filter=HOT&page=1&size=20&sort=likeCount,desc&condition=소고기`
"""
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [
                    Content(
                        mediaType = "application/json",
                        array = ArraySchema(schema = Schema(implementation = FeedResponseDto.FeedItemResponseDto::class))
                    )
                ]
            ),
            SwaggerApiResponse(
                responseCode = "401",
                description = "로그인 필요 (FOLLOWING 필터 사용 시)",
                content = [Content(schema = Schema(implementation = ApiResponse::class))]
            )
        ]
    )
    @GetMapping
    @PageableAsQueryParam
    fun getFeed(
            @Parameter(description = "로그인 사용자 (없으면 null)") @UserInjection userId: Long,
            @Parameter(description = "필터 조건 (ALL/TODAY/HOT/RECOMMEND/FOLLOWING)")
        @RequestParam(name = "filter", required = false) filter: FeedFilterType?,
            @ParameterObject pageable: Pageable,
            @RequestParam(name = "conditon", required = false) condition: String?
    ): ApiResponse<Page<FeedResponseDto.FeedItemResponseDto>>

    @Operation(
        summary = "피드 상세 조회",
        description = """
특정 레시피의 상세 피드 정보를 조회합니다.  

📌 파라미터 설명  
- **user**: 로그인 사용자 (없으면 null)  
- **recipeId**: 조회할 레시피 ID (예: RC-1-00001)  

📌 호출 예시  
- `/api/feed/RC-1-00001`
"""
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "조회 성공",
                content = [Content(schema = Schema(implementation = FeedResponseDto.FeedDetailResponseDto::class))]
            ),
            SwaggerApiResponse(
                responseCode = "404",
                description = "레시피를 찾을 수 없음",
                content = [Content(schema = Schema(implementation = ApiResponse::class))]
            )
        ]
    )
    @GetMapping("/{recipeId}")
    fun getFeedDetail(
            @Parameter(description = "로그인 사용자 (없으면 null)") @UserInjection userId: Long,
            @Parameter(description = "레시피 ID", example = "RC-1-00001") @PathVariable recipeId: String
    ): ApiResponse<FeedResponseDto.FeedDetailResponseDto>
}
