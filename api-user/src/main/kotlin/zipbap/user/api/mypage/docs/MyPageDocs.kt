package zipbap.user.api.mypage.docs

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import io.swagger.v3.oas.annotations.Parameter
import org.springframework.data.domain.Pageable
import org.springdoc.core.converters.models.PageableAsQueryParam
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import zipbap.global.domain.user.User
import zipbap.global.global.auth.resolver.UserInjection
import zipbap.app.global.ApiResponse
import zipbap.user.api.mypage.dto.MyPageResponseDto
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse

@Tag(name = "MyPage", description = "마이페이지 조회 API")
@RequestMapping("/api/mypage")
interface MyPageDocs {

    @Operation(
        summary = "마이페이지 피드 조회",
        description = """
        특정 유저의 마이페이지 **피드 탭**을 조회합니다.
        
        ### 페이징 파라미터
        - **page**: 조회할 페이지 번호 (0부터 시작)
        - **size**: 한 페이지에 포함될 데이터 개수 (기본값: 21)
        - **sort**: 정렬 기준 필드 (`createdAt,desc` 형태로 사용)

        ### 초기 로딩 관련 안내
        마이페이지 진입 시 **초기 화면 구성이 필요**하므로  
        FE에서는 첫 요청 시 `size` 값을 크게 설정하여 **초기 피드 카드를 넉넉히 불러오는 것을 권장**합니다.  
        이후 스크롤에 따라 **무한스크롤 방식**으로 `page` 값을 증가시키며 요청할 수 있습니다.

        ### 페이지 소유자 구분
        - **isOwner = true**: 본인이 자신의 페이지를 조회한 경우  
        - **isOwner = false**: 다른 유저가 조회한 경우  
        """
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "피드 조회 성공",
                content = [Content(schema = Schema(implementation = MyPageResponseDto.MyPageViewDto::class))]
            )
        ]
    )
    @PageableAsQueryParam
    @GetMapping("/{userId}/feed")
    fun getFeedVersion(
        @UserInjection user: User,

        @Parameter(description = "조회할 마이페이지 주인의 userId")
        @PathVariable userId: Long,

        @Parameter(hidden = true) // Pageable은 @PageableAsQueryParam 으로 표시됨
        pageable: Pageable
    ): ApiResponse<MyPageResponseDto.MyPageViewDto>


    @Operation(
        summary = "마이페이지 북마크 조회",
        description = """
        **본인만 조회 가능한 북마크 탭 조회 API**입니다.
        
        - `viewer != owner` 인 경우 → **UNAUTHORIZED** 반환
        
        ### 초기 로딩 관련 안내
        북마크 탭 또한 **처음 진입 시 목록이 빠르게 보여져야** 하므로  
        첫 요청에서 `size` 값을 크게 하여 충분한 데이터를 받아오는 것을 권장합니다.  
        이후 스크롤 시 `page` 를 증가시켜 필요한 데이터만 가져오면 됩니다.

        ### 예시
        ```
        /api/mypage/{userId}/bookmark?page=0&size=40&sort=createdAt,desc
        ```
        """
    )
    @ApiResponses(
        value = [
            SwaggerApiResponse(
                responseCode = "200",
                description = "북마크 조회 성공",
                content = [Content(schema = Schema(implementation = MyPageResponseDto.MyPageViewDto::class))]
            )
        ]
    )
    @PageableAsQueryParam
    @GetMapping("/{userId}/bookmark")
    fun getBookmarkVersion(
        @UserInjection user: User,

        @Parameter(description = "조회 대상 유저 ID (본인만 가능)")
        @PathVariable userId: Long,

        @Parameter(hidden = true)
        pageable: Pageable
    ): ApiResponse<MyPageResponseDto.MyPageViewDto>
}
