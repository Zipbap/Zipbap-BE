package support.extension

import org.springframework.transaction.annotation.Transactional
import support.fixture.RecipeFixture
import zipbap.global.domain.recipe.Recipe
import zipbap.global.domain.recipe.RecipeRepository
import zipbap.global.domain.recipe.RecipeStatus
import zipbap.global.domain.user.User

// 💡 RecipeRepository에 테스트용 save 확장 함수를 추가합니다!
fun RecipeRepository.saveTestRecipe(
    user: User,
    status: RecipeStatus = RecipeStatus.ACTIVE, // 기본값 세팅
    isPrivate: Boolean = false                  // 기본값 세팅
): Recipe {
    // 순수 픽스처 생성 후 저장까지 한 번에 처리
    val recipe = RecipeFixture.create(user).apply {
        this.recipeStatus = status
        this.isPrivate = isPrivate
    }
    return this.save(recipe)
}