package zipbap.global.domain.recipe

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import support.QueryDslTestConfig
import support.annotation.RepositoryTest
import support.extension.saveTestRecipe
import support.fixture.RecipeFixture
import support.fixture.UserFixture
import zipbap.global.domain.user.User
import zipbap.global.domain.user.UserRepository

@RepositoryTest
@Import(QueryDslTestConfig::class) // query DSL 테스트시 수동으로 끌고와줘야함
class RecipeQueryRepositoryTest @Autowired constructor(
    private val recipeRepository: RecipeRepository,
    private val userRepository: UserRepository
) {

    @Test
    @DisplayName("조건 없는 경우 모든 레시피를 조회한다.")
    fun findRecipesWithoutCondition() {
        //given
        val user1 = userRepository.save(UserFixture.create())
        val user2 = userRepository.save(UserFixture.create())

        val recipe1 = recipeRepository.save(RecipeFixture.create(user1))
        val recipe2 = recipeRepository.save(RecipeFixture.create(user1))
        val recipe3 = recipeRepository.save(RecipeFixture.create(user2))

        //when
        val result = recipeRepository.findRecipes(RecipeSearchCondition())


        //then
        Assertions.assertThat(result).hasSize(3)
            .extracting(
                {it.id}, {it.user.id}
            ).containsExactlyInAnyOrderElementsOf(
                listOf(recipe1, recipe2, recipe3).map {
                    tuple(it.id, it.user.id)
                }
            )
    }

    @Test
    @DisplayName("레시피 작성자가 user1이고, 상태가 active이며 공개여부가 private인조건으로 레시피를 필터링한다.")
    fun findRecipesWithTripleConditions() {
        // given
        val user1 = userRepository.save(UserFixture.create())
        val user2 = userRepository.save(UserFixture.create())

        val matrix = setupRecipeMatrix(user1, user2)

        // user1, active, private
        val condition = RecipeSearchCondition(
            userId = user1.id,
            status = RecipeStatus.ACTIVE,
            isPrivate = true
        )

        // when
        val result = recipeRepository.findRecipes(condition)

        // then
        assertThat(result).hasSize(1)
        assertThat(result[0].id).isEqualTo(matrix["u1_active_pub"]!!.id)
    }

    @Test
    @DisplayName("레시피 작성자가 user1이고, 공개여부가 private인 조건으로 레시피를 필터링한다.")
    fun findRecipesWithIdAndIsPrivateConditions() {
        // given
        val user1 = userRepository.save(UserFixture.create())
        val user2 = userRepository.save(UserFixture.create())

        val matrix = setupRecipeMatrix(user1, user2)

        // user1, any, private
        val condition = RecipeSearchCondition(
            userId = user1.id,
            isPrivate = true
        )

        // when
        val result = recipeRepository.findRecipes(condition)

        // then
        assertThat(result).hasSize(2)
            .extracting(
                {it.id}, {it.user.id}
            ).containsExactlyInAnyOrderElementsOf(
                listOf(matrix["u1_active_priv"]!!, matrix["u1_temp_priv"]!!)
                    .map {
                        tuple(it.id, it.user.id)
                    }
            )
    }

    @Test
    @DisplayName("레시피 상태가 active고, 공개 여부가 public인 조건으로 레시피를 필터링한다.")
    fun findRecipesWithStatusAndIsPrivateConditions() {
        // given
        val user1 = userRepository.save(UserFixture.create())
        val user2 = userRepository.save(UserFixture.create())

        val matrix = setupRecipeMatrix(user1, user2)

        // any, active, public
        val condition = RecipeSearchCondition(
            status = RecipeStatus.ACTIVE,
            isPrivate = false
        )

        // when
        val result = recipeRepository.findRecipes(condition)

        // then
        assertThat(result).hasSize(2)
            .extracting(
                {it.id}, {it.user.id}
            ).containsExactlyInAnyOrderElementsOf(
                listOf(matrix["u1_active_pub"]!!, matrix["u2_active_pub"]!!)
                    .map {
                        tuple(it.id, it.user.id)
                    }
            )
    }



    @Test
    @DisplayName("레시피 작성자가 user1이고, 상태가 Active인 조건으로 레시피를 필터링한다.")
    fun findRecipesWithMultiConditions() {
        // given
        val user1 = userRepository.save(UserFixture.create())
        val user2 = userRepository.save(UserFixture.create())

        val matrix = setupRecipeMatrix(user1, user2)

        // user1, active, any
        val condition = RecipeSearchCondition(
            userId = user1.id,
            status = RecipeStatus.ACTIVE
        )

        // when
        val result = recipeRepository.findRecipes(condition)

        // then
        assertThat(result).hasSize(2)
            .extracting(
                {it.id}, {it.user.id}
            ).containsExactlyInAnyOrderElementsOf(
                listOf(matrix["u1_active_pub"]!!, matrix["u1_active_priv"]!!)
                    .map {
                        tuple(it.id, it.user.id)
                    }
            )
    }

    @Test
    @DisplayName("레시피 작성자가 user2인 레시피를 필터링한다.")
    fun findRecipesWithUserConditions() {
        // given
        val user1 = userRepository.save(UserFixture.create())
        val user2 = userRepository.save(UserFixture.create())

        val matrix = setupRecipeMatrix(user1, user2)

        // user2, any, any
        val condition = RecipeSearchCondition(
            userId = user2.id,
        )

        // when
        val result = recipeRepository.findRecipes(condition)

        // then
        assertThat(result).hasSize(4)
            .extracting(
                {it.id}, {it.user.id}
            ).containsExactlyInAnyOrderElementsOf(
                listOf(matrix["u2_active_pub"]!!, matrix["u2_active_priv"]!!, matrix["u2_temp_pub"]!!, matrix["u2_temp_priv"]!!).map {
                    tuple(it.id, it.user.id)
                }
            )
    }

    @Test
    @DisplayName("레시피 상태가 Active인 레시피를 필터링한다.")
    fun findRecipesWithStatusConditions() {
        // given
        val user1 = userRepository.save(UserFixture.create())
        val user2 = userRepository.save(UserFixture.create())

        val matrix = setupRecipeMatrix(user1, user2)

        // any, active, any
        val condition = RecipeSearchCondition(
            status = RecipeStatus.ACTIVE
        )

        // when
        val result = recipeRepository.findRecipes(condition)

        // then
        assertThat(result).hasSize(4)
            .extracting(
                {it.id}, {it.user.id}
            ).containsExactlyInAnyOrderElementsOf(
                listOf(matrix["u1_active_pub"]!!, matrix["u1_active_priv"]!!, matrix["u2_active_pub"]!!, matrix["u2_active_priv"]!!).map {
                    tuple(it.id, it.user.id)
                }
            )
    }

    @Test
    @DisplayName("공개범위가 공개인 레시피를 필터링한다.")
    fun findRecipesWithIsPrivateConditions() {
        // given
        val user1 = userRepository.save(UserFixture.create())
        val user2 = userRepository.save(UserFixture.create())

        val matrix = setupRecipeMatrix(user1, user2)

        // any, any, public
        val condition = RecipeSearchCondition(
            isPrivate = false
        )

        // when
        val result = recipeRepository.findRecipes(condition)

        // then
        assertThat(result).hasSize(4)
            .extracting(
                {it.id}, {it.user.id}
            ).containsExactlyInAnyOrderElementsOf(
                listOf(matrix["u1_active_pub"]!!, matrix["u1_temp_pub"]!!, matrix["u2_active_pub"]!!, matrix["u2_temp_pub"]!!).map {
                    tuple(it.id, it.user.id)
                }
            )
    }

    // =========================================================================
    // 2. findLatestByUserIdOrderByCreatedAtDesc (정렬 및 Limit 검증)
    // =========================================================================

    @Test
    @DisplayName("특정 유저의 가장 최근 레시피 1개만 반환한다.")
    fun findLatestByUserIdOrderByCreatedAtDesc() {
        // given
        val user = userRepository.save(UserFixture.create())

        val oldRecipe = recipeRepository.save(RecipeFixture.create(user))
        Thread.sleep(10) // JPA Auditing 시간 차이를 만들기 위한 미세 딜레이 (필요시)
        val latestRecipe = recipeRepository.save(RecipeFixture.create(user))

        // when
        val result = recipeRepository.findLatestByUserIdOrderByCreatedAtDesc(user.id!!)

        // then
        assertThat(result).isNotNull
        assertThat(result?.id).isEqualTo(latestRecipe.id)
    }

    @Test
    @DisplayName("최근 작성 레시피 조회에서 유저가 작성한 레시피가 없으면 null을 반환한다.")
    fun findLatestByUserId_ReturnsNull_WhenEmpty() {
        // given
        val user1 = userRepository.save(UserFixture.create())
        val user2 = userRepository.save(UserFixture.create())
        recipeRepository.save(RecipeFixture.create(user1))

        // when
        val result = recipeRepository.findLatestByUserIdOrderByCreatedAtDesc(user2.id!!)

        // then
        assertThat(result).isNull()
    }


    // =========================================================================
    // 3. findRecipeDetail (Left Join 검증)
    // =========================================================================

    @Test
    @DisplayName("선택적 연관관계(Category 등)가 없는 레시피도 상세 조회가 가능하다. (Left Join)")
    fun findRecipeDetail_WithOptionalRelationsMissing() {
        // given
        val user = userRepository.save(UserFixture.create())

        // 카테고리 등 선택적 정보가 null인 상태로 레시피 생성
        val incompleteRecipe = RecipeFixture.create(user).apply {
            myCategory = null
            cookingTime = null
        }
        val savedRecipe = recipeRepository.save(incompleteRecipe)

        // when
        val result = recipeRepository.findRecipeDetail(savedRecipe.id!!)

        // then
        assertThat(result).isNotNull

        // 이전에 알려드렸던 assertAll 활용!
        assertAll(
            { assertThat(result?.id).isEqualTo(savedRecipe.id) },
            { assertThat(result?.myCategory).isNull() }, // Left Join이 정상 작동했다면 null이어야 함
            { assertThat(result?.user?.id).isEqualTo(user.id) }
        )
    }

    @Test
    @DisplayName("레시피 상세조회에서 저장되지 않은 id를 조회하려고하면 null이 반환된다.")
    fun findRecipeDetailWithNoSavedId() {
        //given

        //when
        val result = recipeRepository.findRecipeDetail("RC-0-00001")
        //then
        Assertions.assertThat(result).isNull()
    }


    // =========================================================================
    // 4. findAllByMyCategoryIds (Edge Case 검증)
    // =========================================================================

    @Test
    @DisplayName("카테고리 ID 리스트가 비어있을 경우, 조회하려는 유저의 모든 레시피를 조회한다.")
    fun findAllByMyCategoryIds_WithEmptyList() {
        // given
        val user1 = userRepository.save(UserFixture.create())
        val user2 = userRepository.save(UserFixture.create())
        val recipe1 = recipeRepository.save(RecipeFixture.create(user1))
        val recipe2 = recipeRepository.save(RecipeFixture.create(user1))
        val recipe3 = recipeRepository.save(RecipeFixture.create(user2))

        // when
        val emptyCategoryIds = emptyList<String>()
        val result = recipeRepository.findAllByMyCategoryIds(emptyCategoryIds, user1.id!!)

        // then
         assertThat(result).hasSize(2)
             .extracting(
                 {it.id}, {it.user.id}
             ).containsExactlyInAnyOrderElementsOf(
                 listOf(recipe1, recipe2).map {
                     tuple(it.id, it.user.id)
                 }
             )
    }

    private fun setupRecipeMatrix(user1: User, user2: User): Map<String, Recipe> {
        return mapOf(
            "u1_active_pub" to recipeRepository.saveTestRecipe(user1, isPrivate = false, status = RecipeStatus.ACTIVE),
            "u1_active_priv" to recipeRepository.saveTestRecipe(user1, isPrivate = true, status = RecipeStatus.ACTIVE),
            "u1_temp_pub" to recipeRepository.saveTestRecipe(user1, isPrivate = false, status = RecipeStatus.TEMPORARY),
            "u1_temp_priv" to recipeRepository.saveTestRecipe(user1, isPrivate = true, status = RecipeStatus.TEMPORARY),
            "u2_active_priv" to recipeRepository.saveTestRecipe(user2, isPrivate = true, status = RecipeStatus.ACTIVE),
            "u2_active_pub" to recipeRepository.saveTestRecipe(user2, isPrivate = false, status = RecipeStatus.ACTIVE),
            "u2_temp_priv" to recipeRepository.saveTestRecipe(user2, isPrivate = true, status = RecipeStatus.TEMPORARY),
            "u2_temp_pub" to recipeRepository.saveTestRecipe(user2, isPrivate = false, status = RecipeStatus.TEMPORARY)
        )
    }

}