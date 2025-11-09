package zipbap.user.api.mypage.controller

import org.springframework.data.domain.Pageable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import zipbap.user.api.mypage.dto.MyPageResponseDto
import zipbap.user.api.mypage.service.MyPageService
import zipbap.global.domain.user.User
import zipbap.app.global.ApiResponse
import zipbap.user.api.mypage.docs.MyPageDocs


/**
 * 마이페이지 조회 컨트롤러.
 *
 * - `/feed` : 페이지 주인의 피드(레시피 카드 목록)를 조회
 * - `/bookmark` : 페이지 주인의 북마크 목록을 조회 (본인만 접근 가능)
 *
 * Docs(`MyPageDocs`)에서 Swagger 문서 정의를 상속받아 사용한다.
 */
@RestController
@RequestMapping("/api/mypage")
class MyPageController(
    private val myPageService: MyPageService
) : MyPageDocs {

    /**
     * 마이페이지 피드 조회.
     *
     * @param user 현재 로그인한 사용자 (UserInjection)
     * @param userId 조회 대상 사용자 ID (페이지 주인)
     * @param pageable 페이징 정보 (기본 size = 21)
     * @return MyPageResponseDto.MyPageViewDto (프로필 + 피드 카드 + isOwner 플래그 포함)
     */
    override fun getFeedVersion(
        user: User,
        userId: Long,
        pageable: Pageable
    ): ApiResponse<MyPageResponseDto.MyPageViewDto> {
        return ApiResponse.onSuccess(myPageService.getFeedCard(user, userId, pageable))
    }

    /**
     * 마이페이지 북마크 조회.
     *
     * 본인만 조회 가능하며, 다른 사용자가 요청할 경우 UNAUTHORIZED 발생.
     *
     * @param user 현재 로그인한 사용자 (UserInjection)
     * @param userId 조회 대상 사용자 ID (페이지 주인)
     * @param pageable 페이징 정보 (기본 size = 21)
     * @return MyPageResponseDto.MyPageViewDto (프로필 + 북마크 카드 + isOwner = true)
     */
    override fun getBookmarkVersion(
        user: User,
        userId: Long,
        pageable: Pageable
    ): ApiResponse<MyPageResponseDto.MyPageViewDto> {
        return ApiResponse.onSuccess(myPageService.getBookmarkCard(user, userId, pageable))
    }
}
