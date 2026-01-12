package zipbap.global.domain.recipe

import org.springframework.data.repository.query.Param

/**
 * 서비스의 핵심 도메인 중 하나인 Recipe의 조회 로직을 분리하여 확장성 있는 구조를 만들고자 합니다.
 *
 * 조회로직 중심으로 작성
 */
interface RecipeQueryRepository {
    /**
     * 조건을 통해 레시피를 검색합니다.
     * QueryDSL로 작성하여 사용하지 않는 조건은 null로 작성하면 무시됩니다.
     * 상세 조회가 아닌 목록 조회용으로, 2025-01-09 기준 화면에 필요한 join만 사용합니다.
     * findAllByUserIdAndRecipeStatus, findAllByUserIdAndRecipeStatusAndMyCategoryIdIn, findAllByMyCategoryId 통합 버전입니다.
     * @param condition     레시피 검색을 위한 조건을 넣는 data class입니다.
     * @return List of Recipe
     */
    fun findRecipes(condition: RecipeSearchCondition): List<Recipe>

    /**
     * user의 가장 최근에 작성된 레시피 하나를 조회합니다.
     * @param userId        조회할 대상 유저입니다.
     * @return Recipe?
     */
    fun findLatestByUserIdOrderByCreatedAtDesc(userId: Long): Recipe?

    /**
     * MyCategory를 검색 조건으로 해당하는 Recipe를 조회합니다.
     * findRecipes가 기능을 대신할 수 있습니다.
     * @param myCategoryIds     레시피 검색을 위한 myCategory collection
     * @return List of Recipe
     */
    fun findAllByMyCategoryIds(myCategoryIds: Collection<String>): List<Recipe>

    /**
     * Recipe의 모든 연관관계를 조회하는 상세조회입니다.
     * 성능 최적화를 위해 DTO Projection을 사용합니다.
     * @param recipeId      상세조회할 Recipe의 id입니다.
     * @return Recipe?
     */
    fun findRecipeDetail(recipeId: String): Recipe?
}



