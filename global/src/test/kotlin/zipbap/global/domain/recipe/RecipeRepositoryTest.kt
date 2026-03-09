package zipbap.global.domain.recipe

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.tuple
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import support.RepositoryTest
import support.fixture.MyCategoryFixture
import support.fixture.RecipeFixture
import support.fixture.UserFixture
import zipbap.global.domain.category.mycategory.MyCategoryRepository
import zipbap.global.domain.user.UserRepository

@RepositoryTest
class RecipeRepositoryTest @Autowired constructor(
    private val userRepository: UserRepository,
    private val recipeRepository: RecipeRepository,
    private val myCategoryRepository: MyCategoryRepository,
    private val em: EntityManager,
) {


    @Test
    @DisplayName("유저가 작성한 레시피의 개수를 반환한다.")
    fun countByUserId() {
        //given
        val user1 = UserFixture.create()
        val user2 = UserFixture.create()
        userRepository.save(user1)
        userRepository.save(user2)

        val recipe1 = RecipeFixture.create(user = user1)
        val recipe2 = RecipeFixture.create(user = user1)
        val recipe3 = RecipeFixture.create(user = user2)

        recipeRepository.save(recipe1)
        recipeRepository.save(recipe2)
        recipeRepository.save(recipe3)

        //when
        val result1 = recipeRepository.countByUserId(user1.id!!)
        val result2 = recipeRepository.countByUserId(user2.id!!)

        //then
        Assertions.assertThat(result1).isEqualTo(2)
        Assertions.assertThat(result2).isEqualTo(1)

    }

    @Test
    @DisplayName("작성한 유저의 아이디와 레시피의 상태로 레시피를 조회한다.")
    fun findAllByUserIdAndRecipeStatus() {
        //given
        val user = UserFixture.create()
        userRepository.save(user)

        val recipe1 = RecipeFixture.create(user = user, recipeStatus = RecipeStatus.TEMPORARY)
        val recipe2 = RecipeFixture.create(user = user, recipeStatus = RecipeStatus.ACTIVE)
        val savedRecipe1 = recipeRepository.save(recipe1)
        val savedRecipe2 = recipeRepository.save(recipe2)

        //when
        val result =
            recipeRepository.findAllByUserIdAndRecipeStatus(user.id!!, RecipeStatus.ACTIVE)

        //then
        Assertions.assertThat(result).hasSize(1)
            .extracting(Recipe::id, Recipe::title)
            .containsExactlyInAnyOrder(
                tuple(
                    savedRecipe2.id, savedRecipe2.title
                )
            )
    }

    @Test
    @DisplayName("유저가 가장 마지막에 작성한 레시피를 조회한다.")
    fun findTopByUserIdOrderByIdDesc() {
        //given
        val user1 = UserFixture.create()
        val user2 = UserFixture.create()
        val savedUser1 = userRepository.save(user1)
        val savedUser2 = userRepository.save(user2)

        val recipe1 = RecipeFixture.create(savedUser1)
        val recipe2 = RecipeFixture.create(savedUser2)
        val recipe3 = RecipeFixture.create(savedUser1)

        val recipes = recipeRepository.saveAll(listOf(recipe1, recipe2, recipe3))

        //when
        val result = recipeRepository.findTopByUserIdOrderByIdDesc(user1.id!!)

        //then
        Assertions.assertThat(result).isNotNull
        Assertions.assertThat(result!!.id).isEqualTo(recipes[2].id)

    }

    @Test
    @DisplayName("유저의 마이카테고리로 연결된 레시피를 모두 조회한다.")
    fun findAllByMyCategoryId() {
        //given
        val user1 = UserFixture.create()
        val user2 = UserFixture.create()
        val users = userRepository.saveAll(listOf(user1, user2))

        val myCategory1 = MyCategoryFixture.create(users[0], "test")
        val myCategory2 = MyCategoryFixture.create(users[1], "test")
        val myCategories = myCategoryRepository.saveAll(listOf(myCategory1, myCategory2))

        val recipe1 = RecipeFixture.create(user = users[0], recipeStatus = RecipeStatus.ACTIVE,
            myCategory = myCategories[0])
        val recipe2 = RecipeFixture.create(user = users[0], recipeStatus = RecipeStatus.ACTIVE,
            myCategory = myCategories[0])
        val recipe3 = RecipeFixture.create(user = users[1], recipeStatus = RecipeStatus.ACTIVE,
            myCategory = myCategories[1])
        val recipes = recipeRepository.saveAll(listOf(recipe1, recipe2, recipe3))


        //when
        val result = recipeRepository.findAllByMyCategoryId(myCategories[0].id)

        //then
        Assertions.assertThat(result).hasSize(2)
            .extracting(Recipe::id)
            .containsExactlyInAnyOrder(
                tuple(recipes[0].id),
                tuple(recipes[1].id))
    }

    @Test
    @DisplayName("레시피를 조회시 조회수가 +1 증가한다.")
    fun updateViewCount() {
        //given
        val user = UserFixture.create()
        val savedUser = userRepository.save(user)

        val recipe = RecipeFixture.create(user = user, recipeStatus = RecipeStatus.ACTIVE, viewCount = 0L)
        val savedRecipe = recipeRepository.save(recipe)

        //when
        recipeRepository.updateViewCount(savedRecipe.id)
        em.flush()
        em.clear()
        val foundRecipe = recipeRepository.findById(savedRecipe.id).get()


        //then
        Assertions.assertThat(foundRecipe.id).isEqualTo(savedRecipe.id)
        Assertions.assertThat(foundRecipe.viewCount).isEqualTo(1)
    }

}