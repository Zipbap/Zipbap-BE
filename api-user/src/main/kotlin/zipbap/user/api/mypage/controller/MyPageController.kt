package zipbap.user.api.mypage.controller

import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zipbap.global.global.auth.resolver.UserInjection
import zipbap.user.api.mypage.dto.MyPageResponseDto
import zipbap.user.api.mypage.service.MyPageService
import zipbap.global.domain.user.User
import zipbap.app.global.ApiResponse


/**
 * userId 기반으로 마이페이지를 검색합니다.
 * integration -> 페이지 주인의 정보 + 북마크 + 피드
 * bookmark -> 페이지 주인의 정보 + 북마크
 * feed -> 페이지 주인의 정보 + 피드
 * 여기서 userId가 자신인지 아닌지에 따라 리턴해주는 값이 다르도록...
 */
@RestController
@RequestMapping("/api/mypage")
class MyPageController(
        private val myPageService: MyPageService
) {

    /**
     * 마이페이지 중, 피드를 검색합니다!
     */
    @GetMapping("/{userId}/feed")
    fun getFeedVersion(
            @UserInjection user: User,
            @PathVariable userId: Long,
            @PageableDefault(size = 21, page = 0) pageable: Pageable
            ): ApiResponse<MyPageResponseDto.MyPageViewDto> {
        return ApiResponse.onSuccess(myPageService.getFeedCard(user, userId, pageable))
    }

    /**
     * 마이페이지 중 북마크를 검색합니다!
     */
    @GetMapping("/{userId}/bookmark")
    fun getBookmarkVersion(
            @UserInjection user: User,
            @PathVariable userId: Long,
            @PageableDefault(size = 21, page = 0) pageable: Pageable
    ): ApiResponse<MyPageResponseDto.MyPageViewDto> {
        return ApiResponse.onSuccess(myPageService.getBookmarkCard(user, userId, pageable))
    }
}