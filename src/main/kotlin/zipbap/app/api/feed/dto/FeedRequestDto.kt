package zipbap.app.api.feed.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * FeedRequestDto
 *
 * - 피드 요청 DTO 정의
 */
object FeedRequestDto {

    enum class FeedFilterType {
        ALL, TODAY, HOT, RECOMMEND, FOLLOWING
    }

    data class FeedListQuery(
        @Schema(
            description = "필터 (미지정 시 ALL)",
            allowableValues = ["ALL", "TODAY", "HOT", "RECOMMEND", "FOLLOWING"],
            example = "HOT"
        )
        val filter: FeedFilterType = FeedFilterType.ALL
    )
}
