package zipbap.global.domain.cookingorder

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import support.annotation.RepositoryTest
import support.fixture.CookingOrderFixture
import support.fixture.RecipeFixture
import support.fixture.UserFixture
import zipbap.global.domain.recipe.RecipeRepository
import zipbap.global.domain.user.UserRepository

@RepositoryTest
class CookingOrderRepositoryTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val cookingOrderRepository: CookingOrderRepository
){

    @Test
    @DisplayName("특정 레시피에 속한 모든 요리 순서를 제거한다.")
    fun deleteAllByRecipeId() {
        //given
        val user = UserFixture.create()
        val savedUser = userRepository.save(user)

        val recipe1 = RecipeFixture.create(savedUser)
        val savedRecipe = recipeRepository.save(recipe1)

        val cookingOrders1 = CookingOrderFixture.createList(savedRecipe, 3)
        val savedCookingOrders1 = cookingOrderRepository.saveAll(cookingOrders1)

        val result1 = cookingOrderRepository.findAllByRecipeId(savedRecipe.id)
        assertThat(result1).hasSize(3)
            .extracting(CookingOrder::id, {it.recipe.id})
            .containsExactlyInAnyOrderElementsOf(
                savedCookingOrders1.map { tuple(it.id, savedRecipe.id) }
            )

        //when
        cookingOrderRepository.deleteAllByRecipeId(savedRecipe.id)

        //then
        val result2 = cookingOrderRepository.findAllByRecipeId(savedRecipe.id)
        assertThat(result2).isEmpty()
    }

    @Test
    @DisplayName("특정 레시피에 속한 모든 요리순서를 조회한다.")
    fun findAllByRecipeId() {
        //given
        val user = UserFixture.create()
        val savedUser = userRepository.save(user)

        val recipe1 = RecipeFixture.create(savedUser)
        val recipe2 = RecipeFixture.create(savedUser)
        val savedRecipes = recipeRepository.saveAll(listOf(recipe1, recipe2))

        val cookingOrders1 = CookingOrderFixture.createList(savedRecipes[0], 3)
        val cookingOrders2 = CookingOrderFixture.createList(savedRecipes[1], 5)

        val savedCookingOrders1 = cookingOrderRepository.saveAll(cookingOrders1)
        val savedCookingOrders2 = cookingOrderRepository.saveAll(cookingOrders2)

        //when
        val result1 = cookingOrderRepository.findAllByRecipeId(savedRecipes[0].id)
        val result2 = cookingOrderRepository.findAllByRecipeId(savedRecipes[1].id)

        //then
        assertThat(result1).hasSize(3)
            .extracting(CookingOrder::id, {it.recipe.id})
            .containsExactlyInAnyOrderElementsOf(
                savedCookingOrders1.map { tuple(it.id, savedRecipes[0].id) }
            )

        assertThat(result2).hasSize(5)
            .extracting(CookingOrder::id, {it.recipe.id})
            .containsExactlyInAnyOrderElementsOf(
                savedCookingOrders2.map { tuple(it.id, savedRecipes[1].id) }
            )
    }

}