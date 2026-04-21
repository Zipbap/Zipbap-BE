package support.fixture

import zipbap.global.domain.bookmark.Bookmark
import zipbap.global.domain.category.cookingtime.CookingTime
import zipbap.global.domain.category.cookingtype.CookingType
import zipbap.global.domain.category.headcount.Headcount
import zipbap.global.domain.category.level.Level
import zipbap.global.domain.category.mainingredient.MainIngredient
import zipbap.global.domain.category.method.Method
import zipbap.global.domain.category.mycategory.MyCategory
import zipbap.global.domain.category.situation.Situation
import zipbap.global.domain.comment.Comment
import zipbap.global.domain.cookingorder.CookingOrder
import zipbap.global.domain.like.RecipeLike
import zipbap.global.domain.recipe.Recipe
import zipbap.global.domain.recipe.RecipeStatus
import zipbap.global.domain.user.User
import java.util.UUID

object RecipeFixture {

    fun create(
        // 💡 필수 파라미터: User는 무조건 외부에서 주입받아야 JPA 에러가 나지 않습니다.
        user: User,

        // 💡 아래는 모두 기본값이 세팅되어 있어, 테스트에서 필요할 때만 덮어쓰면 됩니다.
        // 주석에 적어주신 RC-{userId}-{sequence} 포맷을 흉내 낸 기본 ID 생성
        id: String = "RC-TEST-${UUID.randomUUID().toString().substring(0, 4)}",

        title: String? = "기본 볶음밥 레시피",
        subtitle: String? = "누구나 5분 만에 뚝딱!",
        introduction: String? = "정말 맛있고 간단한 자취생 필수 요리입니다.",
        thumbnail: String? = "https://cdn.zipbap.com/default_recipe.png",

        // 카테고리들은 기본적으로 연관관계를 맺지 않음 (필요한 테스트에서만 주입)
        myCategory: MyCategory? = null,
        cookingType: CookingType? = null,
        situation: Situation? = null,
        mainIngredient: MainIngredient? = null,
        method: Method? = null,
        headcount: Headcount? = null,
        cookingTime: CookingTime? = null,
        level: Level? = null,

        ingredientInfo: String? = "밥 1공기, 계란 2개, 대파 조금, 굴소스 1스푼",
        video: String? = null,
        kick: String? = "마지막에 참기름 반 스푼",

        isPrivate: Boolean = false, // 테스트 편의를 위해 기본값을 false(공개)로 세팅
        viewCount: Long = 0L,
        recipeStatus: RecipeStatus = RecipeStatus.ACTIVE, // 이것도 임시저장보단 발행 상태가 편합니다

        cookingOrders: MutableList<CookingOrder> = mutableListOf(),
        comments: MutableList<Comment> = mutableListOf(),
        likes: MutableList<RecipeLike> = mutableListOf(),
        bookmarks: MutableList<Bookmark> = mutableListOf()
    ): Recipe {
        return Recipe(
            user = user,
            id = id,
            thumbnail = thumbnail,
            title = title,
            subtitle = subtitle,
            introduction = introduction,
            myCategory = myCategory,
            cookingType = cookingType,
            situation = situation,
            mainIngredient = mainIngredient,
            method = method,
            headcount = headcount,
            cookingTime = cookingTime,
            level = level,
            ingredientInfo = ingredientInfo,
            video = video,
            kick = kick,
            isPrivate = isPrivate,
            viewCount = viewCount,
            recipeStatus = recipeStatus,
            cookingOrders = cookingOrders,
            comments = comments,
            likes = likes,
            bookmarks = bookmarks
        )
    }
}